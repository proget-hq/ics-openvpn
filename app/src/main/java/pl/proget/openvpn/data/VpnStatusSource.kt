package pl.proget.openvpn.data

import android.content.Intent
import de.blinkt.openvpn.core.ConnectionStatus
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicLong

data class VpnStateEvent(val level: ConnectionStatus?, val seq: Long)

object VpnStatusSource {

    private val counter = AtomicLong(0)

    private val _state = MutableStateFlow(VpnStateEvent(level = null, seq = 0))
    val state: StateFlow<VpnStateEvent> = _state.asStateFlow()

    private val listener = object : VpnStatus.StateListener {
        override fun updateState(
            state: String?,
            logmessage: String?,
            localizedResId: Int,
            level: ConnectionStatus?,
            intent: Intent?,
        ) {
            _state.update { VpnStateEvent(level, counter.incrementAndGet()) }
        }

        override fun setConnectedVPN(uuid: String?) = Unit
    }

    init {
        VpnStatus.addStateListener(listener)
    }
}
