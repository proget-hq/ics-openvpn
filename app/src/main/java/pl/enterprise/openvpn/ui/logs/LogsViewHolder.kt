package pl.enterprise.openvpn.ui.logs

import androidx.recyclerview.widget.RecyclerView
import de.blinkt.openvpn.core.LogItem
import pl.enterprise.openvpn.databinding.LogItemBinding
import java.text.SimpleDateFormat
import java.util.*

class LogsViewHolder(private val binding: LogItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun initialize(item: LogItem) {
        binding.text.text = "${formatTime(item.logtime)} ${item.getString(itemView.context)}"
    }

    private fun formatTime(time: Long): String =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(time)
}
