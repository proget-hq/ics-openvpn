package pl.proget.openvpn.ui.logs

import androidx.recyclerview.widget.RecyclerView
import pl.proget.openvpn.databinding.LogItemBinding
import pl.proget.openvpn.logs.LogItem

class LogsViewHolder(private val binding: LogItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun initialize(item: LogItem) {
        binding.text.text = item.toString()
    }
}
