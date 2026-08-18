package pl.proget.openvpn.presentation.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContract

data class Profile(val uri: Uri, val inline: Boolean)

class OvpnProfile : ActivityResultContract<Unit, Profile?>() {

    override fun createIntent(context: Context, input: Unit): Intent =
        Intent(Intent.ACTION_GET_CONTENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("application/x-openvpn-profile")
            .apply {
                MimeTypeMap.getSingleton().let {
                    putExtra(
                        Intent.EXTRA_MIME_TYPES,
                        arrayOf(
                            "application/x-openvpn-profile",
                            "application/openvpn-profile",
                            "application/ovpn",
                            "text/plain",
                            it.getMimeTypeFromExtension("ovpn"),
                            it.getMimeTypeFromExtension("conf"),
                        )
                    )
                }
            }

    override fun parseResult(resultCode: Int, intent: Intent?): Profile? =
        intent?.data?.let { Profile(uri = it, inline = intent.scheme == "inline") }
}
