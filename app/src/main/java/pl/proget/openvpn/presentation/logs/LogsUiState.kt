package pl.proget.openvpn.presentation.logs

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import pl.proget.openvpn.logs.LogItem

data class LogsUiState(
    val logs: PersistentList<LogItem> = persistentListOf(),
    val isLoading: Boolean = true,
)
