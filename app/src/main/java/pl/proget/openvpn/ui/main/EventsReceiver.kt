package pl.proget.openvpn.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import pl.proget.openvpn.Const

class EventsReceiver(private val presenter: MainPresenter) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Const.ACTION_CONFIGURATION_CHANGED -> presenter.onConfigurationChanged()
        }
    }

    fun intentFilter() = IntentFilter().apply {
        addAction(Const.ACTION_CONFIGURATION_CHANGED)
    }
}
