package presentation.core.platform.source.streaming

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import kotlin.coroutines.cancellation.CancellationException

/**
 * Minimal embedded HTTP server that streams MJPEG video from a [SharedFlow] of JPEG frames.
 *
 * Two endpoints are served:
 * - `GET /stream.mjpg` — continuous `multipart/x-mixed-replace` stream.
 * - `GET /snapshot.jpg` — single latest JPEG frame.
 *
 * Uses raw [ServerSocket] so no external HTTP library is required.
 * A separate coroutine is launched per client to allow concurrent viewers.
 *
 * @since 0.1.0
 */
@Single
public class MjpegHttpServer {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null

    @Volatile
    private var latestFrame: ByteArray? = null

    private companion object {
        private const val TAG = "MjpegHttpServer"
        private const val BUFFER_SIZE = 8192
    }

    /**
     * Starts the HTTP server on [port] and begins collecting frames from [frames].
     *
     * A call to [start] while already running will first stop the current server.
     *
     * @param port TCP port to listen on (e.g., 8080).
     * @param frames Flow of JPEG-encoded frames to stream.
     */
    public suspend fun start(port: Int, frames: SharedFlow<ByteArray>) {
        stop()
        var socket: ServerSocket? = null
        try {
            socket = withContext(Dispatchers.IO) {
                // reuseAddress lets us re-bind immediately after the previous server socket
                // enters TIME_WAIT (e.g. after a config change triggers stop() + start()).
                ServerSocket().apply {
                    reuseAddress = true
                    bind(InetSocketAddress(port))
                }
            }
            serverSocket = socket

            serverJob = scope.launch {
                // Keep latest frame available for snapshot requests.
                launch {
                    frames.collect { jpeg -> latestFrame = jpeg }
                }

                // Accept client connections.
                while (isActive) {
                    try {
                        val client = socket.accept()
                        launch { serveClient(client, frames) }
                    } catch (e: Exception) {
                        if (!isActive) break
                        Log.w(TAG, "Accept failed", e)
                    }
                }
            }
            Log.i(TAG, "MJPEG server started on port $port")
        } catch (e: CancellationException) {
            // collectLatest cancelled this call mid-flight — close the socket so the port
            // is released before the next start() attempt, then propagate cancellation.
            socket?.close()
            throw e
        } catch (e: Exception) {
            socket?.close()
            Log.e(TAG, "Failed to start server on port $port", e)
        }
    }

    /** Stops the server and closes all resources. */
    public suspend fun stop() {
        // Cancel first so isActive becomes false, then close the socket to unblock
        // socket.accept() — without this ordering, cancelAndJoin() would deadlock
        // because accept() only returns when a client connects or the socket closes.
        serverJob?.cancel()
        serverSocket?.close()
        serverSocket = null
        serverJob?.join()
        serverJob = null
        latestFrame = null
        Log.i(TAG, "MJPEG server stopped")
    }

    private suspend fun serveClient(client: Socket, frames: SharedFlow<ByteArray>) = withContext(Dispatchers.IO) {
        try {
            client.use { socket ->
                val input = socket.getInputStream().bufferedReader()
                val requestLine = input.readLine() ?: return@withContext

                val path = requestLine.split(" ").getOrElse(1) { "/" }

                when {
                    path.startsWith("/stream") -> serveStream(socket, frames)
                    path.startsWith("/snapshot") -> serveSnapshot(socket)
                    else -> send404(socket)
                }
            }
        } catch (_: Exception) {
            // Client disconnected — normal during streaming.
        }
    }

    private suspend fun serveStream(socket: Socket, frames: SharedFlow<ByteArray>): Unit = withContext(Dispatchers.IO) {
        val output = socket.getOutputStream()
        output.write(
            "HTTP/1.0 200 OK\r\nContent-Type: multipart/x-mixed-replace; boundary=frame\r\nCache-Control: no-cache\r\n\r\n"
                .toByteArray(),
        )
        output.flush()

        frames.collect { jpeg ->
            try {
                output.write("--frame\r\nContent-Type: image/jpeg\r\nContent-Length: ${jpeg.size}\r\n\r\n".toByteArray())
                output.write(jpeg)
                output.write("\r\n".toByteArray())
                output.flush()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun serveSnapshot(socket: Socket) {
        val jpeg = latestFrame
        val output = socket.getOutputStream()
        if (jpeg == null) {
            send404(socket)
            return
        }
        output.write(
            "HTTP/1.0 200 OK\r\nContent-Type: image/jpeg\r\nContent-Length: ${jpeg.size}\r\nCache-Control: no-cache\r\n\r\n"
                .toByteArray(),
        )
        output.write(jpeg)
        output.flush()
    }

    private fun send404(socket: Socket) {
        socket.getOutputStream().write("HTTP/1.0 404 Not Found\r\n\r\n".toByteArray())
    }
}
