package pl.proget.openvpn.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import pl.proget.openvpn.Const

class ConfigurationChangedBroadcastReceiver(
    private val configRepo: ConfigRepo,
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Const.ACTION_CONFIGURATION_CHANGED) {
            configRepo.synchronizeConfiguration()
        }
    }
}
