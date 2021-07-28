package pl.enterprise.openvpn.data

import android.content.Context
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.ProfileManager

fun ProfileManager.save(context: Context, profile: VpnProfile) {
    profiles.forEach {
        removeProfile(context, it)
    }
    addProfile(profile)
    saveProfile(context, profile)
    saveProfileList(context)
}
