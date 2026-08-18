package pl.proget.openvpn.presentation.main

sealed interface MainEvent {
    data class VpnStartRequested(val profileUuid: String) : MainEvent
    data object VpnStopRequested : MainEvent
    data object ProfilePickerRequested : MainEvent
    data object ProfileImportDisallowed : MainEvent
    data object ProfileValidationFailed : MainEvent
    data object ProfileImportFailed : MainEvent
    data object FilePickerNotFound : MainEvent
}
