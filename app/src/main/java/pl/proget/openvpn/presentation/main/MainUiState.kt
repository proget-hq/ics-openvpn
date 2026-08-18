package pl.proget.openvpn.presentation.main

enum class SessionStatus { NotInitiated, Started, AuthFailed }

enum class ConnectionStatus { NotConnected, Connecting, Connected }

enum class InfoMessage { NoConfiguration, ManualConfiguration, ConfiguredByEmm }

enum class MarbleColor { Red, Green, Orange }

enum class SwitchTrack { Default, Green, Orange }

data class MainUiState(
    val sessionStatus: SessionStatus = SessionStatus.NotInitiated,
    val statusMarble: MarbleColor = MarbleColor.Red,
    val connectionStatus: ConnectionStatus = ConnectionStatus.NotConnected,
    val serverName: String? = null,
    val connectionMarble: MarbleColor = MarbleColor.Red,
    val info: InfoMessage = InfoMessage.NoConfiguration,
    val switchVisible: Boolean = true,
    val switchChecked: Boolean = true,
    val switchEnabled: Boolean = true,
    val switchTrack: SwitchTrack = SwitchTrack.Default,
)

fun MainUiState.noConfiguration() = copy(
    sessionStatus = SessionStatus.NotInitiated,
    statusMarble = MarbleColor.Red,
    connectionStatus = ConnectionStatus.NotConnected,
    serverName = null,
    connectionMarble = MarbleColor.Red,
    switchVisible = false,
    info = InfoMessage.NoConfiguration,
)

fun MainUiState.notConnected(imported: Boolean) = copy(
    sessionStatus = SessionStatus.NotInitiated,
    statusMarble = MarbleColor.Red,
    connectionStatus = ConnectionStatus.NotConnected,
    serverName = null,
    connectionMarble = MarbleColor.Red,
    switchVisible = true,
    switchChecked = false,
    info = information(imported),
    switchEnabled = true,
)

fun MainUiState.connected(server: String?, allowDisconnect: Boolean, imported: Boolean) = copy(
    sessionStatus = SessionStatus.Started,
    statusMarble = MarbleColor.Green,
    connectionStatus = ConnectionStatus.Connected,
    serverName = server,
    connectionMarble = MarbleColor.Green,
    info = information(imported),
    switchVisible = true,
    switchChecked = true,
    switchEnabled = allowDisconnect,
    switchTrack = SwitchTrack.Green,
)

fun MainUiState.connecting(server: String?, allowDisconnect: Boolean, imported: Boolean) = copy(
    sessionStatus = SessionStatus.Started,
    statusMarble = MarbleColor.Orange,
    connectionStatus = ConnectionStatus.Connecting,
    serverName = server,
    connectionMarble = MarbleColor.Orange,
    info = information(imported),
    switchVisible = true,
    switchChecked = true,
    switchEnabled = allowDisconnect,
    switchTrack = SwitchTrack.Orange,
)

fun MainUiState.authFailed(imported: Boolean) = copy(
    sessionStatus = SessionStatus.AuthFailed,
    statusMarble = MarbleColor.Red,
    connectionStatus = ConnectionStatus.NotConnected,
    serverName = null,
    connectionMarble = MarbleColor.Red,
    info = information(imported),
    switchChecked = false,
)

fun MainUiState.allowDisconnect(allow: Boolean) = copy(switchEnabled = allow)

private fun information(imported: Boolean) =
    if (imported) InfoMessage.ManualConfiguration else InfoMessage.ConfiguredByEmm
