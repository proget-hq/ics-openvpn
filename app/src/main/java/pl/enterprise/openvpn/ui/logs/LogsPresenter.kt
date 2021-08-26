package pl.enterprise.openvpn.ui.logs

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.enterprise.openvpn.logs.LogFileProvider
import pl.enterprise.openvpn.logs.LogReader

class LogsPresenter(
    private val logFileProvider: LogFileProvider,
) {

    private var view: LogsView? = null

    fun attach(view: LogsView) {
        this.view = view
        CoroutineScope(Dispatchers.IO).launch {
            loadHistoricalLogs()
        }
    }

    fun detach() {
        view = null
    }

    fun onSendLogsClick() {
        logFileProvider.getLogsAsZip()?.let { view?.showSendLogView(it) }
            ?: view?.showNoLogs()
    }

    private fun loadHistoricalLogs() {
        view?.loadLogs(LogReader(logFileProvider).readLogsFromLast(minutes = 5))
    }
}
