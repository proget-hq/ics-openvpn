package pl.enterprise.openvpn

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.StatusListener
import de.blinkt.openvpn.core.VpnStatus
import pl.enterprise.openvpn.data.ConfigRepo
import pl.enterprise.openvpn.logs.LogFileProvider
import pl.enterprise.openvpn.logs.LogListener
import pl.enterprise.openvpn.restrictions.AppRestrictions
import pl.enterprise.openvpn.tools.isAndroidO

class OpenVpnApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (isAndroidO()) {
            createNotificationChannels()
        }

        if (shouldAttachLogger()) {
            VpnStatus.addLogListener(
                LogListener(
                    LogFileProvider(applicationContext),
                    applicationContext,
                    ConfigRepo.getInstance(applicationContext)
                )
            )
        }

        StatusListener().init(applicationContext)
        AppRestrictions.getInstance().checkRestrictions(applicationContext)
    }

    /**
     * Copied from base application
     * @see de.blinkt.openvpn.core.ICSOpenVPNApplication.createNotificationChannels
     */
    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Background message
        manager.createNotificationChannel(
            NotificationChannel(
                OpenVPNService.NOTIFICATION_CHANNEL_BG_ID,
                getString(R.string.channel_name_background),
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = getString(R.string.channel_description_background)
                enableLights(false)
                lightColor = Color.DKGRAY
            }
        )

        // Connection status change messages
        manager.createNotificationChannel(
            NotificationChannel(
                OpenVPNService.NOTIFICATION_CHANNEL_NEWSTATUS_ID,
                getString(R.string.channel_name_status),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.channel_description_status)
                enableLights(true)
                lightColor = Color.BLUE
            }
        )

        // Urgent requests, e.g. two factor auth
        manager.createNotificationChannel(
            NotificationChannel(
                OpenVPNService.NOTIFICATION_CHANNEL_USERREQ_ID,
                getString(R.string.channel_name_userreq),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.channel_description_userreq)
                enableVibration(true)
                lightColor = Color.CYAN
            }
        )
    }

    private fun shouldAttachLogger(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getProcessName() == packageName
        } else {
            try {
                @SuppressLint("PrivateApi") val activityThread =
                    Class.forName("android.app.ActivityThread")
                @SuppressLint("DiscouragedPrivateApi") val getProcessName =
                    activityThread.getDeclaredMethod("currentProcessName")
                getProcessName.invoke(null) as String == packageName
            } catch (ignored: Exception) {
                true
            }
        }
    }
}
