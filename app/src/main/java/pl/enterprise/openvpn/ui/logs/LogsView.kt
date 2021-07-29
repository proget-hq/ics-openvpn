package pl.enterprise.openvpn.ui.logs

import java.io.File

interface LogsView {
    fun showSendLogView(logFile: File)
    fun showNoLogs()
}
