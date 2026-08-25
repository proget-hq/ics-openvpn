package pl.proget.openvpn.presentation.logs

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.receiveAsFlow
import pl.proget.openvpn.R
import pl.proget.openvpn.logs.LogItem
import pl.proget.openvpn.presentation.common.showToast
import pl.proget.openvpn.presentation.theme.ProgetTheme
import java.io.File
import java.util.Date

@PreviewLightDark
@Composable
private fun LogsScreenLoadingPreview() {
    ProgetTheme {
        LogsScreenContent(
            uiState = LogsUiState(),
            onSendLogsClick = {},
            navigateBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun LogsScreenEmptyPreview() {
    ProgetTheme {
        LogsScreenContent(
            uiState = LogsUiState(isLoading = false),
            onSendLogsClick = {},
            navigateBack = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun LogsScreenWithLogsPreview() {
    ProgetTheme {
        LogsScreenContent(
            uiState = LogsUiState(logs = previewLogs(), isLoading = false),
            onSendLogsClick = {},
            navigateBack = {},
        )
    }
}

@Preview(name = "landscape", showBackground = true, device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun LogsScreenLandscapePreview() {
    ProgetTheme {
        LogsScreenContent(
            uiState = LogsUiState(logs = previewLogs(), isLoading = false),
            onSendLogsClick = {},
            navigateBack = {},
        )
    }
}

private fun previewLogs(): PersistentList<LogItem> = persistentListOf(
    LogItem(Date(0), "Building configuration…"),
    LogItem(Date(0), "Waiting for usable network"),
    LogItem(Date(0), "Connected to vpn.example.com:1194"),
    LogItem(Date(0), "Initialization Sequence Completed"),
)

@Composable
fun LogsScreen(
    viewModel: LogsViewModel,
    navigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalActivity.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.receiveAsFlow().collect { event ->
            when (event) {
                is LogsEvent.LogsShareRequested ->
                    activity?.startActivity(shareLogsIntent(context, event.logFile))
                LogsEvent.NoLogsAvailable -> context.showToast(R.string.no_logs_to_share)
                LogsEvent.FailedToPackLogs -> context.showToast(R.string.failed_to_pack_logs)
                LogsEvent.FailedToReadHistoricalLogs -> context.showToast(R.string.failed_to_read_historical_logs)
            }
        }
    }

    LogsScreenContent(
        uiState = uiState,
        onSendLogsClick = viewModel::sendLogsClicked,
        navigateBack = navigateBack,
    )
}

private fun shareLogsIntent(context: Context, logFile: File): Intent {
    val uri = FileProvider.getUriForFile(
        context.applicationContext,
        "${context.packageName}.provider",
        logFile,
    )
    val send = Intent(Intent.ACTION_SEND)
        .setDataAndType(uri, "application/zip")
        .putExtra(Intent.EXTRA_STREAM, uri)
        .putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.logfile_subject))
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    return Intent.createChooser(send, context.getString(R.string.send_logs))
}
