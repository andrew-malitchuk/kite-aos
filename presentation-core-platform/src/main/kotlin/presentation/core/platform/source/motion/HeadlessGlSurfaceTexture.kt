package presentation.core.platform.source.motion

import android.graphics.SurfaceTexture
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.EGLContext
import android.opengl.EGLDisplay
import android.opengl.EGLSurface
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.util.concurrent.CountDownLatch

/**
 * Off-screen, self-draining [SurfaceTexture] used as libausbc's mandatory preview target when we
 * only care about its NV21 data callback.
 *
 * libausbc's `openCameraInternal` requires a real render surface (Surface / SurfaceTexture /
 * SurfaceView / TextureView / GLSurfaceView) and throws `IllegalStateException("Only support
 * ...--null")` on a null one. Worse, it runs on the engine's own camera thread, so that throw is
 * an *uncaught* crash the caller's `runCatching` around `openCamera` cannot intercept — it took
 * the whole process down every time a USB webcam attached. A visible preview is meaningless in a
 * headless foreground service, so we hand the engine this instead: an OES texture bound to a
 * private 1×1 off-screen EGL pbuffer context. The engine renders preview frames into it exactly
 * as it would a TextureView, but nothing is ever shown.
 *
 * The subtlety with *any* dummy SurfaceTexture is buffer back-pressure: libausbc's native preview
 * loop blocks on `ANativeWindow_lock` once the producer's buffers fill, which would also stall the
 * NV21 callback sharing that loop. So we genuinely consume — every
 * [SurfaceTexture.OnFrameAvailableListener] callback runs [SurfaceTexture.updateTexImage] on the
 * GL thread, latching the newest frame and releasing the prior buffer back to the producer so it
 * never blocks. The latched pixels are simply discarded; motion detection reads the separate NV21
 * callback, not this texture.
 *
 * All EGL/GL work is confined to one dedicated thread. [surfaceTexture] is a producer endpoint and
 * may be handed to libausbc from any thread.
 *
 * @param width Preview width to seed the texture's default buffer size with.
 * @param height Preview height to seed the texture's default buffer size with.
 * @since 1.2.0
 */
internal class HeadlessGlSurfaceTexture(width: Int, height: Int) {

    private val thread = HandlerThread("uvc-headless-gl").apply { start() }
    private val handler = Handler(thread.looper)

    private var eglDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var eglContext: EGLContext = EGL14.EGL_NO_CONTEXT
    private var eglSurface: EGLSurface = EGL14.EGL_NO_SURFACE
    private var textureId = 0

    /** The off-screen preview target to hand to `MultiCameraClient.Camera.openCamera`. */
    lateinit var surfaceTexture: SurfaceTexture
        private set

    init {
        // Build EGL + texture + SurfaceTexture synchronously on the GL thread so surfaceTexture is
        // assigned before the constructor returns; the latch also publishes it across threads.
        val ready = CountDownLatch(1)
        handler.post {
            runCatching {
                initEgl()
                textureId = createOesTexture()
                surfaceTexture = SurfaceTexture(textureId).apply {
                    setDefaultBufferSize(width, height)
                    // Delivered on `handler` (the GL thread), where the EGL context is current.
                    setOnFrameAvailableListener({ texture -> drain(texture) }, handler)
                }
            }.onFailure { Log.e(TAG, "Failed to init headless GL surface", it) }
            ready.countDown()
        }
        ready.await()
    }

    /**
     * Latches the newest frame and releases the previous buffer back to the producer; the pixels
     * themselves are never sampled. Runs on the GL thread with the EGL context current.
     */
    private fun drain(texture: SurfaceTexture) {
        runCatching { texture.updateTexImage() }
            .onFailure { Log.w(TAG, "updateTexImage failed", it) }
    }

    /** Tears down the SurfaceTexture, GL texture, EGL context, and GL thread. Idempotent-safe. */
    fun release() {
        handler.post {
            runCatching {
                if (::surfaceTexture.isInitialized) {
                    surfaceTexture.setOnFrameAvailableListener(null)
                    surfaceTexture.release()
                }
                if (textureId != 0) {
                    GLES20.glDeleteTextures(1, intArrayOf(textureId), 0)
                    textureId = 0
                }
                releaseEgl()
            }.onFailure { Log.w(TAG, "Failed to release headless GL surface", it) }
        }
        thread.quitSafely()
    }

    private fun initEgl() {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        check(eglDisplay != EGL14.EGL_NO_DISPLAY) { "eglGetDisplay failed" }
        val version = IntArray(2)
        check(EGL14.eglInitialize(eglDisplay, version, 0, version, 1)) { "eglInitialize failed" }

        val configAttribs = intArrayOf(
            EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
            EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_NONE,
        )
        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfigs = IntArray(1)
        check(
            EGL14.eglChooseConfig(eglDisplay, configAttribs, 0, configs, 0, 1, numConfigs, 0) &&
                numConfigs[0] > 0,
        ) { "eglChooseConfig failed" }
        val config = configs[0]

        val contextAttribs = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
        eglContext = EGL14.eglCreateContext(eglDisplay, config, EGL14.EGL_NO_CONTEXT, contextAttribs, 0)
        check(eglContext != EGL14.EGL_NO_CONTEXT) { "eglCreateContext failed" }

        // A 1×1 pbuffer is enough to make the context current; we never draw into it.
        val pbufferAttribs = intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE)
        eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, config, pbufferAttribs, 0)
        check(eglSurface != EGL14.EGL_NO_SURFACE) { "eglCreatePbufferSurface failed" }

        check(EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) { "eglMakeCurrent failed" }
    }

    private fun createOesTexture(): Int {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, ids[0])
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        return ids[0]
    }

    private fun releaseEgl() {
        if (eglDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            if (eglSurface != EGL14.EGL_NO_SURFACE) EGL14.eglDestroySurface(eglDisplay, eglSurface)
            if (eglContext != EGL14.EGL_NO_CONTEXT) EGL14.eglDestroyContext(eglDisplay, eglContext)
            EGL14.eglReleaseThread()
            EGL14.eglTerminate(eglDisplay)
        }
        eglDisplay = EGL14.EGL_NO_DISPLAY
        eglContext = EGL14.EGL_NO_CONTEXT
        eglSurface = EGL14.EGL_NO_SURFACE
    }

    private companion object {
        private const val TAG = "HeadlessGlSurfaceTexture"
    }
}
