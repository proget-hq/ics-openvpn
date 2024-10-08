package pl.proget.openvpn.logs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.format.DateUtils.DAY_IN_MILLIS
import pl.proget.openvpn.data.ConfigRepo
import java.io.File
import java.io.FileFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class LogFileProvider(private val context: Context) {

    private val logDir = File(context.filesDir, LOGS_DIR_NAME).also { it.mkdirs() }
    private val dayChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            clearOutdatedLogs()
        }
    }
    private val configRepo: ConfigRepo = ConfigRepo.getInstance(context)

    init {
        clearOutdatedLogs()
        context.registerReceiver(dayChangeReceiver, IntentFilter(Intent.ACTION_DATE_CHANGED))
    }

    fun logFile(): File =
        File(
            logDir,
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date())
        )

    fun getLogsAsZip(): File? =
        logDir.listFiles()
            ?.takeIf { it.isNotEmpty() }
            ?.let { files ->
                File(File(context.cacheDir, LOGS_DIR_NAME).also { it.mkdirs() }, "logs.zip")
                    .also {
                        ZipCompressor().zip(files.toList(), it)
                    }
            }

    private fun keepLogsTime() =
        configRepo.fetchConfig().logs
            ?.let { it.keepLogsInDays * DAY_IN_MILLIS }
            ?: DAY_IN_MILLIS

    private fun clearOutdatedLogs() {
        thread {
            logDir.listFiles(
                FileFilter { System.currentTimeMillis() > it.lastModified() + keepLogsTime() })
                ?.forEach {
                    it.delete()
                }
        }
    }

    companion object {
        private const val LOGS_DIR_NAME = "logs"
    }
}
