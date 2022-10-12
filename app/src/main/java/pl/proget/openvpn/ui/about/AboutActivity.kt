package pl.proget.openvpn.ui.about

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import pl.proget.openvpn.BuildConfig
import pl.proget.openvpn.R
import pl.proget.openvpn.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityAboutBinding.inflate(layoutInflater)
            .let {
                binding = it
                setContentView(it.root)
            }

        binding.vpnTitle.text = getString(
            R.string.about_vpn_title, getString(R.string.app), BuildConfig.VERSION_NAME
        )
        binding.vpnCopyright.text = getString(R.string.vpn_copyright, BuildConfig.VERSION_NAME)

        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return true
    }
}
