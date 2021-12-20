package pl.enterprise.openvpn.restrictions

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.IntentFilter
import android.content.RestrictionsManager
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import de.blinkt.openvpn.LaunchVPN
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.Connection
import de.blinkt.openvpn.core.IOpenVPNServiceInternal
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import de.blinkt.openvpn.core.VpnStatus
import java.math.BigInteger
import java.security.MessageDigest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.enterprise.openvpn.Const
import pl.enterprise.openvpn.data.Config
import pl.enterprise.openvpn.data.ConfigRepo
import pl.enterprise.openvpn.data.connection
import pl.enterprise.openvpn.data.hasImportedProfile
import pl.enterprise.openvpn.data.removeProfileFromRestrictions
import pl.enterprise.openvpn.data.save
import pl.enterprise.openvpn.tools.tryIgnoringException

class AppRestrictions private constructor() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            applyRestrictions(context)
        }
    }
    private val mapper = RestrictionsMapper()
    private var checked = false

    @Synchronized
    fun checkRestrictions(context: Context) {
        if (!checked) {
            addChangesListener(context)
            applyRestrictions(context)
            checked = true
        }
    }

    private fun addChangesListener(context: Context) {
        context.registerReceiver(
            receiver,
            IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED)
        )
    }

    private fun applyRestrictions(context: Context) {
        with(context.getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager) {
            applicationRestrictions.run {
                val config = Config(this)
                val configRepo = ConfigRepo.getInstance(context)
                configRepo.updateConfig(config)

                val hash = hash()
                if (hash == runBlocking { configRepo.fetchLastAppRestrictionsHash().first() }) {
                    return
                }

                var vpnProfile: VpnProfile? = null

                if (config.allowImportProfile &&
                    ProfileManager.getInstance(context).hasImportedProfile()
                ) {
                    return
                }

                getString("name")?.takeIf { it.isNotEmpty() }?.let {
                    VpnProfile(it)
                        .apply {
                            mAuthenticationType = mapper.mapAuthType(getString("authType"))
                            mUseLzo = getBoolean("useLzo", true)
                            mCaFilename = mapper.mapCertificate(getString("ca"))
                            mClientCertFilename = mapper.mapCertificate(getString("clientCert"))
                            mClientKeyFilename = mapper.mapCertificate(getString("clientKey"))
                            mPKCS12Filename = mapper.mapCertificate(getString("pkcs12"))
                            mPKCS12Password = getString("pkcs12Password")
                            mUsername = getString("username")
                            mPassword = getString("password")
                            mVersion++

                            applyConnection(getBundle("connection"))
                            mServerName = connection()?.mServerName
                            mServerPort = connection()?.mServerPort
                            applyIpDns(getBundle("ipDns"))
                            applyRouting(getBundle("routing"))
                            applyAuthentication(getBundle("authentication"))
                            applyAdvanced(getBundle("advanced"))
                            applyApplications(getBundle("applications"))
                        }
                        .let { profile ->
                            vpnProfile = profile
                            ProfileManager.getInstance(context)
                                .save(context, profile)
                            VpnStatus.logInfo("AppRestrictions: saved new profile")
                        }
                } ?: ProfileManager.getInstance(context).removeProfileFromRestrictions(context)

                CoroutineScope(Dispatchers.IO).launch {
                    configRepo.insertAppRestrictionsHash(hash)
                }

                context.bindService(
                    Intent(context, OpenVPNService::class.java)
                        .setAction(OpenVPNService.START_SERVICE),
                    object : ServiceConnection {
                        override fun onServiceConnected(
                            p0: ComponentName?,
                            binder: IBinder?
                        ) {
                            IOpenVPNServiceInternal.Stub.asInterface(binder)
                                .run {
                                    stopVPN(false)
                                    vpnProfile?.let { managedConfigurationChanged(it.uuidString, config.autoConnect) }
                                }

                            context.sendBroadcast(Intent(Const.ACTION_CONFIGURATION_CHANGED))
                            if (config.autoConnect && vpnProfile != null) {
                                Intent(context, LaunchVPN::class.java)
                                    .putExtra(LaunchVPN.EXTRA_KEY, vpnProfile!!.uuid.toString())
                                    .setAction(Intent.ACTION_MAIN)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .run {
                                        context.startActivity(this)
                                    }
                            }

                            tryIgnoringException {
                                context.unbindService(this)
                            }
                        }

                        override fun onServiceDisconnected(p0: ComponentName?) {
                            VpnStatus.logInfo("AppRestrictions: vpn service disconnected")
                        }
                    },
                    BIND_AUTO_CREATE
                )
            }
        }
    }

    private fun VpnProfile.applyConnection(bundle: Bundle?) {
        bundle?.run {
            mConnections = arrayOf(
                Connection().apply {
                    mServerName = getString("serverName", "")
                    mServerPort = getInt("serverPort").toString()
                    mUseUdp = getString("protocol", "0") == "0"
                    mProxyType = mapper.mapProxyType(getString("proxy"))
                    mProxyName = getString("proxyAddress")
                    mProxyPort = getString("proxyPort")

                    mUseProxyAuth =
                        getBoolean("enableProxyAuthentication", false)
                    if (mUseProxyAuth) {
                        mProxyAuthUser = getString("proxyUsername")
                        mProxyAuthPassword = getString("proxyUserPassword")
                    }
                    mConnectTimeout = getInt("connectionTimeout")
                }
            )
        }
    }

    private fun VpnProfile.applyIpDns(bundle: Bundle?) {
        bundle?.run {
            mUsePull = getBoolean("useServerIpSettings", true)
            if (!mUsePull) {
                mIPv4Address = getString("ipv4")
                mIPv6Address = getString("ipv6")
            }
            mNobind = getBoolean("noBind")
            mOverrideDNS = getBoolean("overrideDNS", false)
            if (mOverrideDNS) {
                mSearchDomain = getString("searchDomain")
                mDNS1 = getString("dnsServer")
                mDNS2 = getString("dnsAlternativeServer")
            }
        }
    }

    private fun VpnProfile.applyRouting(bundle: Bundle?) {
        bundle?.run {
            mRoutenopull = getBoolean("ignoreRoutes")
            mAllowLocalLAN = getBoolean("allowLocalLAN")
            mBlockUnusedAddressFamilies =
                getBoolean("blockUnusedAddressFamilies")

            mUseDefaultRoute = getBoolean("useDefaultRoutes", true)
            if (!mUseDefaultRoute) {
                mCustomRoutes = getString("customRoutes")
                mExcludedRoutes = getString("excludedRoutes")
            }

            mUseDefaultRoutev6 = getBoolean("useDefaultRoutesV6", true)
            if (!mUseDefaultRoutev6) {
                mCustomRoutesv6 = getString("customRoutesV6")
                mExcludedRoutesv6 = getString("excludedRoutesV6")
            }
        }
    }

    private fun VpnProfile.applyAuthentication(bundle: Bundle?) {
        bundle?.run {
            mExpectTLSCert = getBoolean("expectTlsCert", false)
            mCheckRemoteCN = getBoolean("checkRemoteCN", true)
            if (mCheckRemoteCN) {
                mX509AuthType = mapper.mapX509AuthType(getString("remoteCnType"))
                mRemoteCN = getString("remoteCn")
                mx509UsernameField = getString("x509username")
            }
            mUseTLSAuth = getBoolean("useTls")
            if (mUseTLSAuth) {
                mTLSAuthFilename = mapper.mapCertificate(getString("tlsCert"))
                mTLSAuthDirection = mapper.mapTlsAuthDirection(getString("tlsAuthDirection"))

            }
            mCipher = getString("cipher")

            if (mCipher.isNotEmpty() && mCipher != "AES-256-GCM" && mCipher != "AES-128-GCM") {
                mDataCiphers = "AES-256-GCM:AES-128-GCM:$mCipher"
            }
            mAuth = getString("auth")
        }
    }

    private fun VpnProfile.applyAdvanced(bundle: Bundle?) {
        bundle?.run {
            mPersistTun = getBoolean("usePersistentTun", false)
            mPushPeerInfo = getBoolean("pushPeerInfo", false)
            mUseRandomHostname = getBoolean("useRandomHostname", false)
            mUseFloat = getBoolean("useFloat", false)
            mMssFix = getInt("mssFixValue", 0)
            mTunMtu = getInt("tunMtu")
            mCustomConfigOptions = getString("userOptions")
            mConnectRetryMax = mapper.mapConnectionRetry(getString("connectionRetries"))
            mConnectRetry = getInt("connectionRetryTime", 2).toString()
            mConnectRetryMaxTime = getInt("connectionRetryMaxTime", 300).toString()
        }
    }

    private fun VpnProfile.applyApplications(bundle: Bundle?) {
        bundle?.run {
            mAllowedAppsVpn = mapper.mapApplications(getString("apps"))
            mAllowedAppsVpnAreDisallowed = mAllowedAppsVpn.size > 0 &&
                    getBoolean("useVpnForAllApplications", false)
        }
    }

    private fun Bundle.hash(): String =
        hashConfig(toRawString())

    private fun Bundle.toRawString(): String =
        keySet().map {
            it to when (val value = get(it)) {
                is Array<*> -> handleArray(value)
                is Bundle -> value.toRawString()
                else -> value.toString()
            }
        }.joinToString("\n") { "${it.first}=\"${it.second}\" " }

    private fun handleArray(value: Array<*>) =
        value.joinToString {
            when (val singleValue = it) {
                is Bundle -> singleValue.toRawString()
                else -> singleValue.toString()
            }
        }

    private fun hashConfig(config: String): String =
        try {
            with(MessageDigest.getInstance("SHA1")) {
                config.toByteArray()
                    .let { configBytes ->
                        update(configBytes, 0, configBytes.size)
                        BigInteger(1, digest()).toString(16)
                    }
            }
        } catch (exception: Throwable) {
            ""
        }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: AppRestrictions? = null
        fun getInstance(): AppRestrictions =
            instance ?: AppRestrictions().also { this.instance = it }
    }
}
