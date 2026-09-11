package pl.proget.openvpn.presentation.main

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.blinkt.openvpn.LaunchVPN
import kotlinx.coroutines.flow.receiveAsFlow
import pl.proget.openvpn.R
import pl.proget.openvpn.presentation.common.showToast
import pl.proget.openvpn.presentation.theme.ProgetTheme

@PreviewLightDark
@Composable
private fun MainScreenPreview() {
    ProgetTheme {
        MainScreenContent(
            uiState = MainUiState().noConfiguration(),
            onConnectChanged = {},
            onImportProfileClick = {},
            onLogsClick = {},
            onAboutClick = {},
        )
    }
}

@Preview(name = "landscape", showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun MainScreenLandscapePreview() {
    ProgetTheme {
        MainScreenContent(
            uiState = MainUiState().noConfiguration(),
            onConnectChanged = {},
            onImportProfileClick = {},
            onLogsClick = {},
            onAboutClick = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenNotConnectedPreview() {
    ProgetTheme {
        MainScreenContent(
            uiState = MainUiState().notConnected(imported = false),
            onConnectChanged = {},
            onImportProfileClick = {},
            onLogsClick = {},
            onAboutClick = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenNotConnectedNoDisconnectPreview() {
    ProgetTheme {
        MainScreenContent(
            uiState = MainUiState(allowDisconnect = false).notConnected(imported = false),
            onConnectChanged = {},
            onImportProfileClick = {},
            onLogsClick = {},
            onAboutClick = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenConnectedPreview() {
    ProgetTheme {
        MainScreenContent(
            uiState = MainUiState().connected("vpn.example.com", allowDisconnect = true, imported = true),
            onConnectChanged = {},
            onImportProfileClick = {},
            onLogsClick = {},
            onAboutClick = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenConnectedNoDisconnectPreview() {
    ProgetTheme {
        MainScreenContent(
            uiState = MainUiState().connected("vpn.example.com", allowDisconnect = false, imported = false),
            onConnectChanged = {},
            onImportProfileClick = {},
            onLogsClick = {},
            onAboutClick = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenConnectingPreview() {
    ProgetTheme {
        MainScreenContent(
            uiState = MainUiState().connecting("vpn.example.com", allowDisconnect = true, imported = false),
            onConnectChanged = {},
            onImportProfileClick = {},
            onLogsClick = {},
            onAboutClick = {},
        )
    }
}

@Preview
@Composable
private fun MainScreenAuthFailedAfterConnectedPreview() {
    ProgetTheme {
        MainScreenContent(
            uiState = MainUiState()
                .connected("vpn.example.com", allowDisconnect = false, imported = false)
                .authFailed(imported = false),
            onConnectChanged = {},
            onImportProfileClick = {},
            onLogsClick = {},
            onAboutClick = {},
        )
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navigateToLogs: () -> Unit,
    navigateToAbout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(OvpnProfile()) {
        viewModel.profilePicked(it)
    }

    LaunchedEffect(Unit) {
        viewModel.events.receiveAsFlow().collect { event ->
            when (event) {
                is MainEvent.VpnStartRequested -> activity?.startActivity(
                    Intent(activity, LaunchVPN::class.java)
                        .putExtra(LaunchVPN.EXTRA_KEY, event.profileUuid)
                        .setAction(Intent.ACTION_MAIN)
                )
                MainEvent.ProfilePickerRequested -> try {
                    picker.launch(Unit)
                } catch (e: ActivityNotFoundException) {
                    viewModel.filePickerNotFound()
                }
                MainEvent.ProfileImportDisallowed -> context.showToast(R.string.import_profile_not_allowed)
                MainEvent.ProfileValidationFailed -> context.showToast(R.string.profile_is_invalid)
                MainEvent.ProfileImportFailed -> context.showToast(R.string.import_profile_failed)
                MainEvent.FilePickerNotFound -> context.showToast(R.string.no_app_found)
            }
        }
    }

    MainScreenContent(
        uiState = uiState,
        onConnectChanged = viewModel::connectChanged,
        onImportProfileClick = viewModel::importProfileClicked,
        onLogsClick = { navigateToLogs() },
        onAboutClick = { navigateToAbout() },
    )
}
