package pl.enterprise.openvpn.ui.logs

import pl.enterprise.openvpn.logs.LogItem
import java.io.File

interface LogsView {
    fun showSendLogView(logFile: File)
    fun showNoLogs()
    fun loadLogs(logs: List<LogItem>)
}
