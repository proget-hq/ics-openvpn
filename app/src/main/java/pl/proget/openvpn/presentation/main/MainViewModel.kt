package pl.proget.openvpn.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.blinkt.openvpn.VpnProfile
import de.blinkt.openvpn.core.ProfileManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    
    val uiState: StateFlow<MainUiState>
        field = MutableStateFlow(MainUiState())

    val events: ReceiveChannel<MainEvent>
        field = Channel<MainEvent>(capacity = Channel.BUFFERED)

    init {
        if (profileManager.profiles.isNullOrEmpty()) uiState.update { it.noConfiguration() }

        viewModelScope.launch {
            vpnStates.collect(::reduceVpnState)
        }

        viewModelScope.launch {
            configRepo.changes.collect { config ->
                if (profile() == null) uiState.update { it.noConfiguration() }
                else uiState.update { it.allowDisconnect(config.allowDisconnect) }
            }
        }
    }

    fun connectChanged(checked: Boolean) {
        uiState.update { it.copy(switchChecked = checked) }
        val profile = profile()
        when {
            profile == null -> uiState.update { it.noConfiguration() }
            checked -> events.trySend(MainEvent.VpnStartRequested(profile.uuid.toString()))
            else -> events.trySend(MainEvent.VpnStopRequested)
        }
    }

     fun importProfileClicked() {
        if (config().allowImportProfile) events.trySend(MainEvent.ProfilePickerRequested)
        else events.trySend(MainEvent.ProfileImportDisallowed)
    }

    fun filePickerNotFound() {
        events.trySend(MainEvent.FilePickerNotFound)
    }

    fun profilePicked(picked: Profile?) {
        if (picked == null) return
        viewModelScope.launch {
            when (importer.import(picked.uri, picked.inline)) {
                ImportResult.Success -> uiState.update { it.notConnected(imported = true) }
                ImportResult.InvalidProfile -> events.trySend(MainEvent.ProfileValidationFailed)
                ImportResult.Failed -> events.trySend(MainEvent.ProfileImportFailed)
            }
        }
    }

    private fun profile(): VpnProfile? = profileManager.profiles.firstOrNull()

    private fun config(): Config = configRepo.fetchConfig()

    private fun reduceVpnState(event: VpnStateEvent) {
        val level = event.level ?: return
        val profile = profile() ?: return uiState.update { it.noConfiguration() }
        val connection = profile.connection()
            ?: return uiState.update { it.notConnected(profile.isImported()) }
        val isImported = profile.isImported()
        val server = connection.mServerName
        uiState.update {
            when (level) {
                VpnLevel.LEVEL_CONNECTED -> it.connected(server, config().allowDisconnect, isImported)
                VpnLevel.LEVEL_AUTH_FAILED -> it.authFailed(isImported)
                VpnLevel.LEVEL_NOTCONNECTED -> it.notConnected(isImported)
                else -> it.connecting(server, config().allowDisconnect, isImported)
            }
        }
    }
}
