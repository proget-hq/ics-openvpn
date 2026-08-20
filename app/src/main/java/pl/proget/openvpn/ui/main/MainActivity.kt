package pl.proget.openvpn.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import de.blinkt.openvpn.core.ConfigParser
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import pl.proget.openvpn.Const
import pl.proget.openvpn.R
import pl.proget.openvpn.data.ConfigRepo
import pl.proget.openvpn.data.MainServiceConnection
import pl.proget.openvpn.data.isImported
import pl.proget.openvpn.data.isValid
import pl.proget.openvpn.data.save
import pl.proget.openvpn.presentation.navigation.Navigation
import pl.proget.openvpn.presentation.theme.ProgetTheme
import pl.proget.openvpn.ui.logs.LogsActivity

class MainActivity : AppCompatActivity() {
    private val presenter by lazy {
        MainPresenter(
            ProfileManager.getInstance(this),
            ConfigRepo.getInstance(this)
        )
    }
    private val eventsReceiver by lazy { EventsReceiver(presenter) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProgetTheme {
                Navigation()
            }
        }

        Intent(this, OpenVPNService::class.java)
            .setAction(OpenVPNService.START_SERVICE)
            .run {
                this@MainActivity.bindService(
                    this, MainServiceConnection, BIND_AUTO_CREATE
                )
            }

        ContextCompat.registerReceiver(
            this,
            eventsReceiver,
            eventsReceiver.intentFilter(),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        unbindService(MainServiceConnection)
        MainServiceConnection.clear()
        unregisterReceiver(eventsReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.importProfile -> presenter.onImportProfileClick()
            R.id.logs -> startActivity(Intent(this, LogsActivity::class.java))
            R.id.about -> presenter.onAboutClick()
        }

        return true
    }
}
