package pl.enterprise.openvpn.ui.main

import de.blinkt.openvpn.VpnProfile
import java.io.File

interface MainView {
    fun showNoConfiguration()
    fun showNotConnected(importedProfile: Boolean)
    fun showConnected(
        serverName: String,
        allowDisconnect: Boolean,
        importedProfile: Boolean
    )

    fun showAuthFailed(importedProfile: Boolean)
    fun showConnecting(
        serverName: String,
        allowDisconnect: Boolean,
        importedProfile: Boolean
    )

    fun startVpn(profile: VpnProfile)
    fun stopVpn()
    fun showAboutView()
    fun showSendLogView(logFile: File)
    fun showNoLogs()
    fun showFilePicker()
    fun showImportProfileDisallowed()
}
