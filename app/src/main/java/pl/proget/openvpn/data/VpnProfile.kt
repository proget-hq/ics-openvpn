package pl.proget.openvpn.data

import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.Connection
import pl.proget.openvpn.Const
import pl.proget.openvpn.tools.tryIgnoringException

fun VpnProfile.connection(): Connection? =
    tryIgnoringException { mConnections?.get(0) }

fun VpnProfile.isImported(): Boolean =
    importedProfileHash == Const.IMPORTED_PROFILE_HASH

fun VpnProfile.isValid(): Boolean =
    mConnections.isNotEmpty() && name.isNotEmpty()
