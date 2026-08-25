package pl.proget.openvpn.data

import android.content.Context
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import pl.proget.openvpn.logs.LogItem
import java.util.Date

fun vpnLogs(context: Context): Flow<LogItem> = callbackFlow {
    val listener = VpnStatus.LogListener { logItem ->
        val date = Date(logItem.logtime)
        val message = logItem.getString(context)
        trySend(LogItem(date, message))
    }
    VpnStatus.addLogListener(listener)
    awaitClose { VpnStatus.removeLogListener(listener) }
}.buffer(Channel.UNLIMITED)
