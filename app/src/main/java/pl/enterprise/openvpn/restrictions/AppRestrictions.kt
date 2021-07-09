package pl.enterprise.openvpn.restrictions

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.RestrictionsManager
import android.os.Bundle
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.Connection
import de.blinkt.openvpn.core.ProfileManager

class AppRestrictions private constructor() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
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
                getString("name")?.let {
                    VpnProfile(it)
                        .apply {
                            mAuthenticationType = mapper.mapAuthType(getString("authType"));
                            mUseLzo = getBoolean("useLzo", true);
                            mCaFilename = mapper.mapCertificate(getString("ca"));
                            mClientCertFilename = mapper.mapCertificate(getString("clientCert"));
                            mClientKeyFilename = mapper.mapCertificate(getString("clientKey"));
                            mPKCS12Filename = mapper.mapCertificate(getString("pkcs12"));
                            mPKCS12Password = getString("pkcs12Password");
                            mUsername = getString("username");
                            mPassword = getString("password");

                            applyConnection(getBundle("connection"))
                            applyIpDns(getBundle("ipDns"))
                            applyRouting(getBundle("routing"))
                            applyAuthentication(getBundle("authentication"))
                            applyAdvanced(getBundle("advanced"))
                            applyApplications(getBundle("applications"))
                        }
                        .let {
                            ProfileManager.getInstance(context)
                                .run {
                                    addProfile(it)
                                    saveProfile(context, it)
                                    saveProfileList(context)
                                }
                        }
                }
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

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: AppRestrictions? = null
        fun getInstance(): AppRestrictions =
            instance ?: AppRestrictions().also { this.instance = it }
    }
}
