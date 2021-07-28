package pl.enterprise.openvpn.data

import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.Connection
import pl.enterprise.openvpn.Const

fun VpnProfile.connection(): Connection? =
    mConnections?.get(0)

fun VpnProfile.isImported(): Boolean =
    importedProfileHash == Const.IMPORTED_PROFILE_HASH
