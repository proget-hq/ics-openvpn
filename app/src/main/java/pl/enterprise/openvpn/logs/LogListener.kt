package pl.enterprise.openvpn.logs

import android.content.Context
import de.blinkt.openvpn.core.LogItem
import de.blinkt.openvpn.core.VpnStatus
import pl.enterprise.openvpn.data.ConfigRepo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogListener(
    private val provider: LogFileProvider,
    private val context: Context,
    private val configRepo: ConfigRepo
) : VpnStatus.LogListener {

    override fun newLog(logItem: LogItem) {
        configRepo.fetchConfig().logs?.run {
            if (logLevels.contains(logItem.logLevel)) {
                provider.logFile()
                    .appendText("${logItem.getTime()} ${logItem.getString(context)} \n")
            }
        }
    }

    private fun LogItem.getTime(): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(logtime))
}
