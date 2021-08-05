package pl.enterprise.openvpn.data

import android.content.Context
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.ProfileManager
import pl.enterprise.openvpn.Const

fun ProfileManager.save(context: Context, profile: VpnProfile) {
    profiles.forEach {
        removeProfile(context, it)
    }
    addProfile(profile)
    saveProfile(context, profile)
    saveProfileList(context)
}

fun ProfileManager.removeProfileFromRestrictions(context: Context) {
    profiles.filterNot { it.importedProfileHash == Const.IMPORTED_PROFILE_HASH }
        .forEach { removeProfile(context, it) }
}

fun ProfileManager.hasImportedProfile(): Boolean =
    profiles.any { it.importedProfileHash == Const.IMPORTED_PROFILE_HASH }
