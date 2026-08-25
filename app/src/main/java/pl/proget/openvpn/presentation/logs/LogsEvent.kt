package pl.proget.openvpn.presentation.logs

import java.io.File

sealed interface LogsEvent {
    data class LogsShareRequested(val logFile: File) : LogsEvent
    data object NoLogsAvailable : LogsEvent
    data object FailedToReadHistoricalLogs : LogsEvent
    data object FailedToPackLogs : LogsEvent
}
