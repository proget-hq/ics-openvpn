package pl.proget.openvpn.logs

import android.content.Context
import de.blinkt.openvpn.core.LogItem
import de.blinkt.openvpn.core.VpnStatus
import pl.proget.openvpn.CalendarApi.dateFormatter
import pl.proget.openvpn.data.ConfigRepo
import java.util.Date

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
        dateFormatter().format(Date(logtime))
}
