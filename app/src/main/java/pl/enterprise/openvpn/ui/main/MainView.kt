package pl.enterprise.openvpn.ui.main

import de.blinkt.openvpn.VpnProfile
import java.io.File

interface MainView {
    fun showNoConfiguration()
    fun showNotConnected()
    fun showConnected(
        serverName: String,
        allowDisconnect: Boolean
    )

    fun showAuthFailed()
    fun showConnecting(
        serverName: String,
        allowDisconnect: Boolean
    )

    fun startVpn(profile: VpnProfile)
    fun stopVpn()
    fun showAboutView()
    fun showSendLogView(logFile: File)
    fun showNoLogs()
}
