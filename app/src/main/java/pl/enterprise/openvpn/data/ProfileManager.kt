package pl.enterprise.openvpn.data

import android.content.Context
import androidx.core.content.edit
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.Preferences
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.ProfileManager.saveProfile
import pl.enterprise.openvpn.Const

fun ProfileManager.save(
    context: Context,
    profile: VpnProfile
) {
    profiles.forEach {
        removeProfile(context, it)
    }
    addProfile(profile)
    saveProfile(context, profile)
    saveProfileList(context)
    Preferences.getDefaultSharedPreferences(context)
        .let { prefs ->
            prefs.edit {
                putString("alwaysOnVpn", profile.uuidString)
            }
        }
}

fun ProfileManager.removeProfileFromRestrictions(context: Context) {
    profiles.filterNot { it.importedProfileHash == Const.IMPORTED_PROFILE_HASH }
        .forEach { removeProfile(context, it) }
}

fun ProfileManager.hasImportedProfile(): Boolean =
    profiles.any { it.importedProfileHash == Const.IMPORTED_PROFILE_HASH }
