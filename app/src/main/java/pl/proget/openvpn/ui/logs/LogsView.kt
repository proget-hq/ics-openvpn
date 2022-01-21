package pl.proget.openvpn.ui.logs

import pl.proget.openvpn.logs.LogItem
import java.io.File

interface LogsView {
    fun showSendLogView(logFile: File)
    fun showNoLogs()
    fun loadLogs(logs: List<LogItem>)
}
