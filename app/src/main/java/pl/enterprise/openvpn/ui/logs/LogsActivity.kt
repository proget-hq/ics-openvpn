package pl.enterprise.openvpn.ui.logs

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import de.blinkt.openvpn.core.VpnStatus
import pl.enterprise.openvpn.R
import pl.enterprise.openvpn.databinding.ActivityLogsBinding
import pl.enterprise.openvpn.logs.LogFileProvider
import java.io.File

class LogsActivity : AppCompatActivity(), LogsView {

    private lateinit var binding: ActivityLogsBinding
    private val presenter by lazy { LogsPresenter(LogFileProvider(this)) }
    private val adapter = LogsAdapter()
    private val logListener = VpnStatus.LogListener {
        runOnUiThread { adapter.add(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityLogsBinding.inflate(layoutInflater)
            .let {
                binding = it
                setContentView(it.root)
            }

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        VpnStatus.addLogListener(logListener)
        presenter.attach(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logs_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.send_logs -> presenter.onSendLogsClick()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        VpnStatus.removeLogListener(logListener)
    }

    override fun showSendLogView(logFile: File) {
        FileProvider.getUriForFile(applicationContext, "${packageName}.provider", logFile)
            .let {
                Intent(Intent.ACTION_SEND)
                    .setData(it)
                    .putExtra(Intent.EXTRA_STREAM, it)
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.logfile_subject))
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .run {
                        runOnUiThread {
                            this@LogsActivity.startActivity(
                                Intent.createChooser(this, getString(R.string.send_logs))
                            )
                        }
                    }
            }
    }

    override fun showNoLogs() {
        Toast.makeText(this, "No logs to share", Toast.LENGTH_SHORT).show()
    }
}
