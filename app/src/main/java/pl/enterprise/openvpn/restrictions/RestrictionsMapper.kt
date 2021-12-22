package pl.enterprise.openvpn.restrictions

import android.util.Base64
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.Connection
import java.nio.charset.StandardCharsets

class RestrictionsMapper {

    fun mapAuthType(value: String?): Int =
        when (value) {
            "0" -> VpnProfile.TYPE_CERTIFICATES
            "1" -> VpnProfile.TYPE_PKCS12
            "2" -> VpnProfile.TYPE_KEYSTORE
            "3" -> VpnProfile.TYPE_USERPASS
            "4" -> VpnProfile.TYPE_STATICKEYS
            "5" -> VpnProfile.TYPE_USERPASS_CERTIFICATES
            "6" -> VpnProfile.TYPE_USERPASS_PKCS12
            "7" -> VpnProfile.TYPE_USERPASS_KEYSTORE
            "8" -> VpnProfile.TYPE_EXTERNAL_APP
            else -> VpnProfile.TYPE_CERTIFICATES
        }

    fun mapCertificate(value: String?): String? =
        value?.takeIf { it.isNotEmpty() }?.let {
            VpnProfile.INLINE_TAG + String(
                Base64.decode(it, Base64.DEFAULT),
                StandardCharsets.UTF_8
            )
        }

    fun mapProxyType(value: String?): Connection.ProxyType =
        when (value) {
            "1" -> Connection.ProxyType.HTTP
            "2" -> Connection.ProxyType.SOCKS5
            "3" -> Connection.ProxyType.ORBOT
            else -> Connection.ProxyType.NONE
        }

    fun mapX509AuthType(value: String?): Int =
        when (value) {
            "1" -> VpnProfile.X509_VERIFY_TLSREMOTE_RDN
            "2" -> VpnProfile.X509_VERIFY_TLSREMOTE_RDN_PREFIX
            else -> VpnProfile.X509_VERIFY_TLSREMOTE_DN
        }

    fun mapTlsAuthDirection(value: String?): String =
        when (value) {
            "0" -> "0"
            "1" -> "1"
            "3" -> "tls-crypt"
            "4" -> "tls-crypt-v2"
            else -> ""
        }

    fun mapConnectionRetry(value: String?): String =
        when (value) {
            "0" -> "1"
            "1" -> "2"
            "2" -> "5"
            "3" -> "50"
            else -> "-1"
        }

    fun mapApplications(value: String?): HashSet<String> =
        value?.split(";")?.toHashSet() ?: hashSetOf()
}
