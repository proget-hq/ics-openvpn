package pl.enterprise.openvpn.ui.main

import android.content.Intent
import de.blinkt.openvpn.core.Connection
import de.blinkt.openvpn.core.ConnectionStatus
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VpnStatus
import pl.enterprise.openvpn.data.ConfigRepo
import pl.enterprise.openvpn.logs.LogFileProvider

class MainPresenter(
    private val manager: ProfileManager,
    private val logFileProvider: LogFileProvider,
    private val configRepo: ConfigRepo
) : VpnStatus.StateListener {

    private var view: MainView? = null

    fun attach(view: MainView) {
        this.view = view

        if (manager.profiles.isNullOrEmpty()) {
            view.showNoConfiguration()
        }

        VpnStatus.addStateListener(this)
    }

    fun detach() {
        view = null
        VpnStatus.removeStateListener(this)
    }

    override fun updateState(
        state: String?,
        logmessage: String?,
        localizedResId: Int,
        level: ConnectionStatus?,
        Intent: Intent?
    ) {
        if (level != null) {
            getConnection()?.run {
                when (level) {
                    ConnectionStatus.LEVEL_CONNECTED -> view?.showConnected(
                        mServerName,
                        configRepo.fetchConfig().allowDisconnect
                    )
                    ConnectionStatus.LEVEL_AUTH_FAILED -> view?.showAuthFailed()
                    ConnectionStatus.LEVEL_NOTCONNECTED -> view?.showNotConnected()
                    else -> view?.showConnecting(
                        mServerName,
                        configRepo.fetchConfig().allowDisconnect
                    )
                }
            }
        }
    }

    override fun setConnectedVPN(uuid: String?) {
    }

    fun onConnectChanged(checked: Boolean) {
        with(manager.profiles.firstOrNull()) {
            when {
                this == null -> view?.showNoConfiguration()
                checked -> view?.startVpn(this)
                else -> view?.stopVpn()
            }
        }
    }

    fun onSendLogsClick() {
        logFileProvider.getLogsAsZip()?.let { view?.showSendLogView(it) }
            ?: view?.showNoLogs()
    }

    fun onAboutClick() {
        view?.showAboutView()
    }

    private fun getConnection(): Connection? =
        manager.profiles.firstOrNull()?.mConnections?.get(0)
}
