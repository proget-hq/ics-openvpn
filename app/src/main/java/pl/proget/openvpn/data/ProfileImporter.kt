package pl.proget.openvpn.data

import android.content.Context
import android.net.Uri
import de.blinkt.openvpn.core.ConfigParser
import de.blinkt.openvpn.core.ProfileManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.proget.openvpn.Const

enum class ImportResult { Success, InvalidProfile, Failed }

class ProfileImporter(context: Context) {
    private val appContext = context.applicationContext

    suspend fun import(uri: Uri, inline: Boolean): ImportResult =
        withContext(Dispatchers.IO) {
            val stream = if (inline) "".byteInputStream()
            else appContext.contentResolver.openInputStream(uri)

            stream.use { stream ->
                try {
                    val import = ConfigParser().run {
                        parseConfig(stream?.reader())
                        convertProfile().let { profile ->
                            if (profile.isValid()) {
                                profile.mName = Const.IMPORTED_PROFILE_NAME
                                profile.importedProfileHash = Const.IMPORTED_PROFILE_HASH
                                ProfileManager.getInstance(appContext).save(appContext, profile)
                                ImportResult.Success
                            } else {
                                ImportResult.InvalidProfile
                            }
                        }
                    }

                    return@withContext import
                } catch (e: Exception) {
                    return@withContext ImportResult.Failed
                }
            }
        }
}
