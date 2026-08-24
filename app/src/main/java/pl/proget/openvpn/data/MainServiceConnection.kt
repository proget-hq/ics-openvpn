package pl.proget.openvpn.data

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import de.blinkt.openvpn.core.IOpenVPNServiceInternal

object MainServiceConnection: ServiceConnection {
    private var vpnService: IOpenVPNServiceInternal? = null

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        vpnService = IOpenVPNServiceInternal.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        clear()
    }

    fun clear() {
        vpnService = null
    }

    fun stopVpn() {
        vpnService?.stopVPN(false)
    }
}
