package pl.enterprise.openvpn.ui.logs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.blinkt.openvpn.core.LogItem
import pl.enterprise.openvpn.databinding.LogItemBinding

class LogsAdapter : RecyclerView.Adapter<LogsViewHolder>() {

    private val list: MutableList<LogItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsViewHolder =
        LogsViewHolder(
            LogItemBinding.inflate(LayoutInflater.from(parent.context))
        )

    override fun onBindViewHolder(holder: LogsViewHolder, position: Int) {
        holder.initialize(list[position])
    }

    override fun getItemCount(): Int =
        list.size

    fun add(item: LogItem) {
        list.add(item)
        notifyItemInserted(list.size)
    }
}
