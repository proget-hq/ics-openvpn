package pl.enterprise.openvpn.ui.logs

import pl.enterprise.openvpn.logs.LogFileProvider

class LogsPresenter(
    private val logFileProvider: LogFileProvider,
) {

    private var view: LogsView? = null

    fun attach(view: LogsView) {
        this.view = view
    }

    fun detach() {
        view = null
    }

    fun onSendLogsClick() {
        logFileProvider.getLogsAsZip()?.let { view?.showSendLogView(it) }
            ?: view?.showNoLogs()
    }
}
