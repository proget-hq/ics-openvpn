package pl.proget.openvpn.presentation.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.blinkt.openvpn.core.VpnStatus
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.proget.openvpn.logs.LogFileProvider
import pl.proget.openvpn.logs.LogItem
import pl.proget.openvpn.logs.LogReader
import java.io.IOException

class LogsViewModel(
    private val logFileProvider: LogFileProvider,
    private val logs: Flow<LogItem>,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    val uiState: StateFlow<LogsUiState>
        field = MutableStateFlow(LogsUiState())

    val events: ReceiveChannel<LogsEvent>
        field = Channel<LogsEvent>(capacity = Channel.BUFFERED)

    init {
        viewModelScope.launch {
            val historical = try {
                withContext(ioDispatcher) {
                    LogReader(logFileProvider).readLogsFromLast(HISTORY_MINUTES)
                }
            } catch (e: IOException) {
                VpnStatus.logException("Reading log history", e)
                events.trySend(LogsEvent.FailedToReadHistoricalLogs)
                emptyList()
            }
            uiState.update { it.copy(logs = historical.toPersistentList(), isLoading = false) }

            logs.collect { item -> uiState.update { state ->
                state.copy(logs = state.logs.adding(item)) }
            }
        }
    }

    fun sendLogsClicked() {
        viewModelScope.launch {
            try {
                val zip = withContext(ioDispatcher) { logFileProvider.getLogsAsZip() }
                events.trySend(
                    zip?.let(LogsEvent::LogsShareRequested) ?: LogsEvent.NoLogsAvailable
                )
            } catch (e: IOException) {
                events.trySend(
                    LogsEvent.FailedToPackLogs
                )
            }
        }
    }

    private companion object {
        const val HISTORY_MINUTES = 5
    }
}
