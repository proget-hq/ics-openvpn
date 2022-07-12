package pl.proget.openvpn.ui.main

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.blinkt.openvpn.LaunchVPN
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.ConfigParser
import de.blinkt.openvpn.core.IOpenVPNServiceInternal
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.ProfileManager
import pl.proget.openvpn.Const
import pl.proget.openvpn.R
import pl.proget.openvpn.data.ConfigRepo
import pl.proget.openvpn.data.isImported
import pl.proget.openvpn.data.isValid
import pl.proget.openvpn.data.save
import pl.proget.openvpn.databinding.ActivityMainBinding
import pl.proget.openvpn.ui.about.AboutActivity
import pl.proget.openvpn.ui.logs.LogsActivity

class MainActivity : AppCompatActivity(), MainView {
    private val presenter by lazy {
        MainPresenter(
            ProfileManager.getInstance(this),
            ConfigRepo.getInstance(this)
        )
    }
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
    private val eventsReceiver by lazy { EventsReceiver(presenter) }

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

        binding.connectSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                presenter.onConnectChanged(isChecked)
            }
        }
        presenter.attach(this)
        registerReceiver(eventsReceiver, eventsReceiver.intentFilter())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        unbindService(serviceConnection)
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

    override fun showImportProfileDisallowed() {
        Toast.makeText(this, R.string.import_profile_not_allowed, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            IMPORT_PROFILE_REQUEST_CODE -> data?.data?.let {
                try {
                    val inputStream = if (data.scheme == "inline") "".byteInputStream()
                    else contentResolver.openInputStream(it)

                    ConfigParser().run {
                        parseConfig(inputStream?.reader())
                        convertProfile().let { profile ->
                            if (profile.isValid()) {
                                profile.isImported()
                                profile.mName = Const.IMPORTED_PROFILE_NAME
                                profile.importedProfileHash = Const.IMPORTED_PROFILE_HASH
                                ProfileManager.getInstance(this@MainActivity)
                                    .save(this@MainActivity, profile)
                                presenter.onProfileImported()
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    R.string.profile_is_invalid,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, R.string.import_profile_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun showFilePicker() {
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
                            it.getMimeTypeFromExtension("conf")
                        )
                    )
                }
            }
            .let {
                startActivityForResult(it, IMPORT_PROFILE_REQUEST_CODE)
            }
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

    override fun showNotConnected(importedProfile: Boolean) {
        runOnUiThread {
            binding.status.text = getString(R.string.not_initiated)
            binding.statusMarble.setRed()
            binding.connection.text = getString(R.string.not_connected)
            binding.connectionMarble.setRed()
            binding.connectSwitch.visibility = VISIBLE
            changeConnectSwitch(false)
            binding.info.text = information(importedProfile)
            binding.connectSwitch.isEnabled = true
        }
    }

    override fun showConnected(
        serverName: String,
        allowDisconnect: Boolean,
        importedProfile: Boolean
    ) {
        runOnUiThread {
            binding.status.text = getString(R.string.started)
            binding.statusMarble.setGreen()
            binding.connection.text = getString(R.string.connected_to, serverName)
            binding.connectionMarble.setGreen()
            binding.info.text = information(importedProfile)
            binding.connectSwitch.visibility = VISIBLE
            changeConnectSwitch(true)
            binding.connectSwitch.isEnabled = allowDisconnect
        }
    }

    override fun showAuthFailed(importedProfile: Boolean) {
        runOnUiThread {
            binding.status.text = getString(R.string.state_auth_failed)
            binding.statusMarble.setRed()
            binding.connection.text = getString(R.string.not_connected)
            binding.connectionMarble.setRed()
            binding.info.text = information(importedProfile)
            changeConnectSwitch(false)
        }
    }

    override fun showConnecting(
        serverName: String,
        allowDisconnect: Boolean,
        importedProfile: Boolean
    ) {
        runOnUiThread {
            binding.status.text = getString(R.string.started)
            binding.statusMarble.setOrange()
            binding.connection.text = getString(R.string.connecting_to, serverName)
            binding.connectionMarble.setOrange()
            binding.info.text = information(importedProfile)
            binding.connectSwitch.visibility = VISIBLE
            changeConnectSwitch(true)
            binding.connectSwitch.isEnabled = allowDisconnect
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

    override fun showAboutView() {
        Intent(this, AboutActivity::class.java)
            .run { this@MainActivity.startActivity(this) }
    }

    private fun changeConnectSwitch(expected: Boolean) {
        if (binding.connectSwitch.isChecked != expected) {
            binding.connectSwitch.isChecked = expected
        }
    }

    private fun information(importedProfile: Boolean): String =
        getString(
            if (importedProfile) R.string.manual_configuration
            else R.string.configured_by_emm
        )

    companion object {
        private const val IMPORT_PROFILE_REQUEST_CODE = 1
    }
}
