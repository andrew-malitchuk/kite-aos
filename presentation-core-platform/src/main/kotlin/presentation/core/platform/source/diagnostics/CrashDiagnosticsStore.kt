package presentation.core.platform.source.diagnostics

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.koin.core.annotation.Single
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Synchronous, crash-safe storage for crash reports and the relaunch rate-limiter state.
 *
 * **Deliberately backed by [SharedPreferences] rather than Proto DataStore**, unlike every other
 * preference in the app. This store is written from an
 * [Thread.UncaughtExceptionHandler] in a process that is about to die: DataStore's API is
 * `suspend`, and a coroutine scheduled during teardown is not guaranteed to run before the process
 * is gone. [SharedPreferences.Editor.commit] writes on the calling thread and returns only once the
 * bytes are on disk, which is exactly the guarantee a crash path needs.
 *
 * For the same reason the *user setting* for crash relaunch is mirrored here by
 * [CrashRelaunchSettingMirror] rather than read from DataStore at crash time.
 *
 * @param context Application context used for the preferences file and the report directory.
 * @see CrashRelaunchHandler
 * @since 2.2.0
 */
@Single
public class CrashDiagnosticsStore(private val context: Context) {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /** Directory holding the persisted crash reports, created on first use. */
    private val reportDirectory: File
        get() = File(context.filesDir, REPORT_DIR).apply { if (!exists()) mkdirs() }

    /**
     * Whether the user has crash auto-relaunch switched on.
     *
     * Defaults to `true` so a device that crashes before the mirror has ever been written still
     * recovers — the feature is on by default, and a missing mirror must not silently disable it.
     *
     * @since 2.2.0
     */
    public var isRelaunchEnabled: Boolean
        get() = preferences.getBoolean(KEY_RELAUNCH_ENABLED, true)
        set(value) {
            preferences.edit().putBoolean(KEY_RELAUNCH_ENABLED, value).apply()
        }

    /**
     * Records a crash and reports whether a relaunch should be scheduled.
     *
     * The rate limiter is the whole point of this method. A deterministic startup crash combined
     * with an unconditional relaunch is a boot loop that will flatten the battery and leave a
     * wall-mounted panel unrecoverable without physically attaching ADB. Once
     * [MAX_CRASHES_IN_WINDOW] crashes have occurred inside [CRASH_WINDOW_MS], relaunching stops and
     * the device is allowed to come to rest.
     *
     * @param throwable The fatal exception being recorded.
     * @return `true` when a relaunch should be scheduled, `false` when the rate limit has tripped.
     * @since 2.2.0
     */
    public fun recordCrashAndAllowRelaunch(throwable: Throwable): Boolean {
        val now = System.currentTimeMillis()
        writeReport(throwable, now)

        // Keep only the timestamps still inside the rolling window, then add this crash.
        val recent = readTimestamps().filter { now - it < CRASH_WINDOW_MS } + now
        // commit(), not apply(): the process is about to die and apply()'s background write
        // may never land, which would reset the limiter and re-enable the boot loop.
        preferences.edit()
            .putString(KEY_CRASH_TIMES, recent.joinToString(separator = ","))
            .commit()

        return recent.size < MAX_CRASHES_IN_WINDOW
    }

    /**
     * Returns the stored crash reports, newest first.
     *
     * @return Report file contents, at most [MAX_REPORTS] entries.
     * @since 2.2.0
     */
    public fun readReports(): List<String> =
        reportDirectory.listFiles()
            ?.sortedByDescending { it.name }
            ?.mapNotNull { runCatching { it.readText() }.getOrNull() }
            .orEmpty()

    /**
     * Deletes every stored crash report and clears the rate-limiter history.
     *
     * @since 2.2.0
     */
    public fun clear() {
        reportDirectory.listFiles()?.forEach { it.delete() }
        preferences.edit().remove(KEY_CRASH_TIMES).apply()
    }

    /** Parses the comma-separated crash timestamps, tolerating a corrupt or absent value. */
    private fun readTimestamps(): List<Long> =
        preferences.getString(KEY_CRASH_TIMES, null)
            ?.split(',')
            ?.mapNotNull(String::toLongOrNull)
            .orEmpty()

    /**
     * Writes one crash report and prunes the directory to [MAX_REPORTS].
     *
     * Failures are swallowed: this runs while the process is already dying, and an exception here
     * would replace a useful crash with a confusing one.
     */
    private fun writeReport(throwable: Throwable, timestampMillis: Long) {
        try {
            val stamp = SimpleDateFormat(FILE_TIMESTAMP_FORMAT, Locale.US).format(Date(timestampMillis))
            val report = buildString {
                appendLine("timestamp: $stamp")
                appendLine("thread: ${Thread.currentThread().name}")
                appendLine()
                appendLine(throwable.stackTraceToString())
            }
            File(reportDirectory, "crash-$stamp.txt").writeText(report)
            prune()
        } catch (e: Exception) {
            Log.w(TAG, "Could not persist crash report", e)
        }
    }

    /** Keeps only the newest [MAX_REPORTS] report files. */
    private fun prune() {
        val files = reportDirectory.listFiles()?.sortedByDescending { it.name } ?: return
        files.drop(MAX_REPORTS).forEach { it.delete() }
    }

    /**
     * Tuning constants for crash diagnostics and the relaunch rate limiter.
     *
     * @since 2.2.0
     */
    public companion object {
        private const val TAG = "CrashDiagnosticsStore"
        private const val PREFS_NAME = "kite_crash_diagnostics"
        private const val REPORT_DIR = "diagnostics"
        private const val KEY_CRASH_TIMES = "crash_times"
        private const val KEY_RELAUNCH_ENABLED = "relaunch_enabled"
        private const val FILE_TIMESTAMP_FORMAT = "yyyyMMdd-HHmmss"

        /** How many reports are kept on disk before the oldest are dropped. */
        private const val MAX_REPORTS = 10

        /**
         * Crashes allowed inside [CRASH_WINDOW_MS] before relaunching is suppressed.
         *
         * Three is enough to ride out a transient fault (a bad network state, a one-off renderer
         * kill) while still catching a deterministic startup crash after a few seconds rather than
         * looping until the battery is flat.
         */
        public const val MAX_CRASHES_IN_WINDOW: Int = 3

        /** Rolling window for the crash counter, in milliseconds (10 minutes). */
        public const val CRASH_WINDOW_MS: Long = 10 * 60 * 1000L
    }
}
