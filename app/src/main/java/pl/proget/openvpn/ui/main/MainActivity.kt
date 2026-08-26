package pl.proget.openvpn.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import de.blinkt.openvpn.core.OpenVPNService
import pl.proget.openvpn.data.MainServiceConnection
import pl.proget.openvpn.presentation.navigation.Navigation
import pl.proget.openvpn.presentation.theme.ProgetTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
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
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(MainServiceConnection)
        MainServiceConnection.clear()
    }
}
