package pl.proget.openvpn.ui.logs

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.blinkt.openvpn.core.VpnStatus
import pl.proget.openvpn.R
import pl.proget.openvpn.databinding.ActivityLogsBinding
import pl.proget.openvpn.logs.LogFileProvider
import pl.proget.openvpn.logs.LogItem
import java.io.File
import java.util.Date

class LogsActivity : AppCompatActivity(), LogsView {

    private lateinit var binding: ActivityLogsBinding
    private val presenter by lazy { LogsPresenter(LogFileProvider(this)) }
    private val adapter = LogsAdapter()
    private val logListener = VpnStatus.LogListener {
        runOnUiThread { adapter.add(LogItem(Date(it.logtime), it.getString(this))) }
    }
    private val scrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                (recyclerView.layoutManager as LinearLayoutManager).let {
                    if (it.findFirstVisibleItemPosition() == 0 || it.findLastVisibleItemPosition() == adapter.itemCount - 1) {
                        binding.root.transitionToState(R.id.logs_loaded)
                    }
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            with(binding.root) {
                if (progress == 0f || progress == 1f) {
                    if (dy > 5 && currentState != R.id.down_button_visible) {
                        transitionToState(R.id.logs_loaded)
                        transitionToState(R.id.down_button_visible)
                    }
                    if (dy < -5 && currentState != R.id.up_button_visible) {
                        transitionToState(R.id.logs_loaded)
                        transitionToState(R.id.up_button_visible)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recycler.adapter = adapter
        binding.recycler.layoutManager = SpeedyLinearLayoutManager(this)
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.arrowDown.setOnClickListener {
            binding.recycler.smoothScrollToPosition(adapter.itemCount)
        }
        binding.arrowUp.setOnClickListener {
            binding.recycler.smoothScrollToPosition(0)
        }
        binding.recycler.addOnScrollListener(scrollListener)
        presenter.attach(this)
    }

    override fun loadLogs(logs: List<LogItem>) {
        runOnUiThread {
            binding.root.transitionToState(R.id.logs_loaded)
            adapter.add(*logs.toTypedArray())
            VpnStatus.addLogListener(logListener)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.logs_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.send_logs -> presenter.onSendLogsClick()
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detach()
        binding.recycler.removeOnScrollListener(scrollListener)
        VpnStatus.removeLogListener(logListener)
    }

    override fun showSendLogView(logFile: File) {
        FileProvider.getUriForFile(applicationContext, "${packageName}.provider", logFile)
            .let {
                Intent(Intent.ACTION_SEND)
                    .setData(it)
                    .putExtra(Intent.EXTRA_STREAM, it)
                    .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.logfile_subject))
                    .setType("application/zip")
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
