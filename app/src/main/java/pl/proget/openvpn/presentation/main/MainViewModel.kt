package pl.proget.openvpn.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.ProfileManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.proget.openvpn.data.Config
import pl.proget.openvpn.data.ConfigRepo
import pl.proget.openvpn.data.ImportResult
import pl.proget.openvpn.data.ProfileImporter
import pl.proget.openvpn.data.VpnStateEvent
import pl.proget.openvpn.data.VpnStatusSource
import pl.proget.openvpn.data.connection
import pl.proget.openvpn.data.isImported
import de.blinkt.openvpn.core.ConnectionStatus as VpnLevel

class MainViewModel(
    private val profileManager: ProfileManager,
    private val configRepo: ConfigRepo,
    private val importer: ProfileImporter,
) : ViewModel() {

    private val vpnStates: StateFlow<VpnStateEvent> = VpnStatusSource.state

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _events = Channel<MainEvent>(capacity = Channel.BUFFERED)
    val events: Flow<MainEvent> = _events.receiveAsFlow()

    init {
        if (profileManager.profiles.isNullOrEmpty()) _uiState.update { it.noConfiguration() }

        viewModelScope.launch {
            vpnStates.collect(::reduceVpnState)
        }

        viewModelScope.launch {
            configRepo.changes.collect { config ->
                if (profile() == null) _uiState.update { it.noConfiguration() }
                else _uiState.update { it.allowDisconnect(config.allowDisconnect) }
            }
        }
    }

    fun connectChanged(checked: Boolean) {
        _uiState.update { it.copy(switchChecked = checked) }
        val profile = profile()
        when {
            profile == null -> _uiState.update { it.noConfiguration() }
            checked -> _events.trySend(MainEvent.VpnStartRequested(profile.uuid.toString()))
            else -> _events.trySend(MainEvent.VpnStopRequested)
        }
    }

    fun filePickerNotFound() {
        _events.trySend(MainEvent.FilePickerNotFound)
    }

    fun profilePicked(picked: Profile?) {
        if (picked == null) return
        viewModelScope.launch {
            when (importer.import(picked.uri, picked.inline)) {
                ImportResult.Success -> _uiState.update { it.notConnected(imported = true) }
                ImportResult.InvalidProfile -> _events.trySend(MainEvent.ProfileValidationFailed)
                ImportResult.Failed -> _events.trySend(MainEvent.ProfileImportFailed)
            }
        }
    }

    private fun profile(): VpnProfile? = profileManager.profiles.firstOrNull()

    private fun config(): Config = configRepo.fetchConfig()

    private fun reduceVpnState(event: VpnStateEvent) {
        val level = event.level ?: return
        val profile = profile() ?: return _uiState.update { it.noConfiguration() }
        val connection = profile.connection()
            ?: return _uiState.update { it.notConnected(profile.isImported()) }
        val isImported = profile.isImported()
        val server = connection.mServerName
        _uiState.update {
            when (level) {
                VpnLevel.LEVEL_CONNECTED -> it.connected(server, config().allowDisconnect, isImported)
                VpnLevel.LEVEL_AUTH_FAILED -> it.authFailed(isImported)
                VpnLevel.LEVEL_NOTCONNECTED -> it.notConnected(isImported)
                else -> it.connecting(server, config().allowDisconnect, isImported)
            }
        }
    }
}
