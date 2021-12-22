package pl.enterprise.openvpn.ui.logs

import androidx.recyclerview.widget.RecyclerView
import pl.enterprise.openvpn.databinding.LogItemBinding
import pl.enterprise.openvpn.logs.LogItem

class LogsViewHolder(private val binding: LogItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun initialize(item: LogItem) {
        binding.text.text = item.toString()
    }
}
