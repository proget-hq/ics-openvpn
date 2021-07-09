package pl.enterprise.openvpn.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import de.blinkt.openvpn.LaunchVPN
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.IOpenVPNServiceInternal
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import pl.enterprise.openvpn.R
import pl.enterprise.openvpn.data.ConfigRepo
import pl.enterprise.openvpn.databinding.ActivityMainBinding
import pl.enterprise.openvpn.logs.LogFileProvider
import pl.enterprise.openvpn.ui.about.AboutActivity
import java.io.File

class MainActivity : AppCompatActivity(), MainView {
    private var presenter: MainPresenter? = null
    private var service: IOpenVPNServiceInternal? = null
    private lateinit var binding: ActivityMainBinding

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
        }

        override fun onServiceConnected(
            name: ComponentName?,
            service: IBinder?
        ) {
            this@MainActivity.service = IOpenVPNServiceInternal.Stub.asInterface(service)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ActivityMainBinding.inflate(layoutInflater)
            .let {
                binding = it
                setContentView(it.root)
            }

        Intent(this, OpenVPNService::class.java)
            .setAction(OpenVPNService.START_SERVICE)
            .run {
                this@MainActivity.bindService(
                    this, serviceConnection, BIND_AUTO_CREATE
                )
            }

        presenter = MainPresenter(
            ProfileManager.getInstance(this),
            LogFileProvider(this),
            ConfigRepo.getInstance(this)
        )
        binding.connectSwitch.setOnCheckedChangeListener { _, isChecked ->
            presenter?.onConnectChanged(isChecked)
        }
        presenter?.attach(this)
    }

    override fun showNoLogs() {
        Toast.makeText(this, "No logs to share", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.detach()
        unbindService(serviceConnection)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sendLogs -> presenter?.onSendLogsClick()
            R.id.about -> presenter?.onAboutClick()
        }

        return true
    }

    override fun showNoConfiguration() {
        runOnUiThread {
            binding.status.text = getString(R.string.not_initiated)
            binding.statusMarble.setRed()
            binding.connection.text = getString(R.string.not_connected)
            binding.connectionMarble.setRed()
            binding.connectSwitch.visibility = GONE
            binding.info.text = getString(R.string.no_configuration)
        }
    }

    override fun showNotConnected() {
        runOnUiThread {
            binding.status.text = getString(R.string.not_initiated)
            binding.statusMarble.setRed()
            binding.connection.text = getString(R.string.not_connected)
            binding.connectionMarble.setRed()
            binding.connectSwitch.visibility = VISIBLE
            changeConnectSwitch(false)
            binding.info.text = getString(R.string.configured_by_emm)
            binding.connectSwitch.isClickable = true
        }
    }

    override fun showConnected(
        serverName: String,
        allowDisconnect: Boolean
    ) {
        runOnUiThread {
            binding.status.text = getString(R.string.started)
            binding.statusMarble.setGreen()
            binding.connection.text = getString(R.string.connected_to, serverName)
            binding.connectionMarble.setGreen()
            binding.info.text = getString(R.string.configured_by_emm)
            changeConnectSwitch(true)
            binding.connectSwitch.isClickable = allowDisconnect
        }
    }

    override fun showAuthFailed() {
        runOnUiThread {
            binding.status.text = getString(R.string.state_auth_failed)
            binding.statusMarble.setRed()
            binding.connection.text = getString(R.string.not_connected)
            binding.connectionMarble.setRed()
            binding.info.text = getString(R.string.configured_by_emm)
            changeConnectSwitch(false)
        }
    }

    override fun showConnecting(
        serverName: String,
        allowDisconnect: Boolean
    ) {
        runOnUiThread {
            binding.status.text = getString(R.string.started)
            binding.statusMarble.setOrange()
            binding.connection.text = getString(R.string.connecting_to, serverName)
            binding.connectionMarble.setOrange()
            binding.info.text = getString(R.string.configured_by_emm)
            changeConnectSwitch(true)
            binding.connectSwitch.isClickable = allowDisconnect
        }
    }

    override fun startVpn(profile: VpnProfile) {
        Intent(this, LaunchVPN::class.java)
            .putExtra(LaunchVPN.EXTRA_KEY, profile.uuid.toString())
            .setAction(Intent.ACTION_MAIN)
            .run {
                this@MainActivity.startActivity(this)
            }
    }

    override fun stopVpn() {
        service?.stopVPN(false)
    }

    override fun showSendLogView(logFile: File) {
        FileProvider.getUriForFile(applicationContext, "pl.enterprise.openvpn.provider", logFile)
            .let {
                Intent(Intent.ACTION_SEND)
                    .setData(it)
                    .putExtra(Intent.EXTRA_STREAM, it)
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.logfile_subject))
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .run {
                        runOnUiThread {
                            this@MainActivity.startActivity(
                                Intent.createChooser(this, getString(R.string.send_logs))
                            )
                        }
                    }
            }
    }

    override fun showAboutView() {
        Intent(this, AboutActivity::class.java)
            .run { this@MainActivity.startActivity(this) }
    }

    private fun changeConnectSwitch(expected: Boolean) {
        if (binding.connectSwitch.isChecked != expected) {
            binding.connectSwitch.isChecked = expected
        }
    }
}
