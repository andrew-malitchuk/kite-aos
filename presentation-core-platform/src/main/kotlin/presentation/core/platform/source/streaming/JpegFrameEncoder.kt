package presentation.core.platform.source.streaming

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import org.koin.core.annotation.Single
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

/**
 * Encodes camera frames from any source into JPEG byte arrays for the MJPEG stream.
 *
 * Centralises JPEG encoding so all motion sources (CameraX on mobile, Camera2 external and
 * UVC on TV) feed the same [MjpegHttpServer] frame flow with identical output. Owns a single
 * reusable [ByteArrayOutputStream] to avoid per-frame allocations; it is only ever used from
 * a single camera/analysis thread.
 *
 * @see MjpegHttpServer
 * @since 1.2.0
 */
@Single
public class JpegFrameEncoder {

    // Reused across frames to avoid churning large buffers. Single-threaded access only.
    private val outputStream = ByteArrayOutputStream(INITIAL_BUFFER_BYTES)

    /**
     * Encodes an already-decoded [bitmap], optionally rotating it by [rotationDegrees] first.
     *
     * Used by the CameraX path, which decodes the frame via `ImageProxy.toBitmap()`. The input
     * bitmap is recycled when a rotation copy is made; callers should not reuse it afterwards.
     *
     * @param bitmap Source frame.
     * @param rotationDegrees Clockwise rotation to apply (0 = none).
     * @param quality JPEG quality 0–100.
     * @return Encoded JPEG bytes.
     * @since 1.2.0
     */
    public fun encodeFromBitmap(bitmap: Bitmap, rotationDegrees: Int, quality: Int): ByteArray {
        val frame = if (rotationDegrees != 0) {
            val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
            bitmap.recycle()
            rotated
        } else {
            bitmap
        }
        outputStream.reset()
        frame.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        frame.recycle()
        return outputStream.toByteArray()
    }

    /**
     * Encodes a raw NV21 buffer (UVC webcam frames) to JPEG.
     *
     * @param nv21 NV21-formatted bytes (Y plane followed by interleaved VU).
     * @param width Frame width.
     * @param height Frame height.
     * @param rotationDegrees Clockwise rotation to apply (0 = none).
     * @param quality JPEG quality 0–100.
     * @return Encoded JPEG bytes.
     * @since 1.2.0
     */
    public fun encodeFromNv21(
        nv21: ByteArray,
        width: Int,
        height: Int,
        rotationDegrees: Int,
        quality: Int,
    ): ByteArray {
        outputStream.reset()
        YuvImage(nv21, ImageFormat.NV21, width, height, null)
            .compressToJpeg(Rect(0, 0, width, height), quality, outputStream)
        val jpeg = outputStream.toByteArray()
        return if (rotationDegrees != 0) rotateJpeg(jpeg, rotationDegrees, quality) else jpeg
    }

    /**
     * Encodes a Camera2 `YUV_420_888` [image] to JPEG by first packing it into NV21.
     *
     * `YuvImage` has no direct `YUV_420_888` constructor, so NV21 is used as the common
     * intermediate. Row and pixel strides are honored so padded planes decode correctly.
     *
     * @param image The Camera2 image (format `YUV_420_888`). Not closed here — the caller owns it.
     * @param rotationDegrees Clockwise rotation to apply (0 = none).
     * @param quality JPEG quality 0–100.
     * @return Encoded JPEG bytes.
     * @since 1.2.0
     */
    public fun encodeFromYuv420(image: Image, rotationDegrees: Int, quality: Int): ByteArray {
        val nv21 = yuv420ToNv21(image)
        return encodeFromNv21(nv21, image.width, image.height, rotationDegrees, quality)
    }

    // Re-encodes an already-compressed JPEG after rotating it (NV21/YUV paths, uncommon on TV).
    private fun rotateJpeg(jpeg: ByteArray, rotationDegrees: Int, quality: Int): ByteArray {
        val decoded = android.graphics.BitmapFactory.decodeByteArray(jpeg, 0, jpeg.size)
            ?: return jpeg
        return encodeFromBitmap(decoded, rotationDegrees, quality)
    }

    private companion object {
        private const val INITIAL_BUFFER_BYTES = 64 * 1024

        // Packs a YUV_420_888 image into a contiguous NV21 byte array (Y plane, then interleaved
        // V,U), honoring each plane's rowStride/pixelStride so padding bytes are excluded.
        private fun yuv420ToNv21(image: Image): ByteArray {
            val width = image.width
            val height = image.height
            val ySize = width * height
            val nv21 = ByteArray(ySize + ySize / 2)

            val yPlane = image.planes[0]
            copyPlane(yPlane.buffer, yPlane.rowStride, yPlane.pixelStride, width, height, nv21, 0, 1)

            // Interleave V then U into the second half (NV21 order is VU).
            val uPlane = image.planes[1]
            val vPlane = image.planes[2]
            val chromaWidth = width / 2
            val chromaHeight = height / 2
            interleaveChroma(vPlane, uPlane, chromaWidth, chromaHeight, nv21, ySize)
            return nv21
        }

        private fun copyPlane(
            buffer: ByteBuffer,
            rowStride: Int,
            pixelStride: Int,
            width: Int,
            height: Int,
            out: ByteArray,
            outOffset: Int,
            outPixelStride: Int,
        ) {
            var outIndex = outOffset
            for (row in 0 until height) {
                var col = 0
                var bufferPos = row * rowStride
                while (col < width) {
                    out[outIndex] = buffer.get(bufferPos)
                    outIndex += outPixelStride
                    bufferPos += pixelStride
                    col++
                }
            }
        }

        private fun interleaveChroma(
            vPlane: Image.Plane,
            uPlane: Image.Plane,
            chromaWidth: Int,
            chromaHeight: Int,
            out: ByteArray,
            outOffset: Int,
        ) {
            val vBuffer = vPlane.buffer
            val uBuffer = uPlane.buffer
            var outIndex = outOffset
            for (row in 0 until chromaHeight) {
                var col = 0
                while (col < chromaWidth) {
                    out[outIndex++] = vBuffer.get(row * vPlane.rowStride + col * vPlane.pixelStride)
                    out[outIndex++] = uBuffer.get(row * uPlane.rowStride + col * uPlane.pixelStride)
                    col++
                }
            }
        }
    }
}
