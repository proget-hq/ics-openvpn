package pl.enterprise.openvpn.logs

import android.text.format.DateUtils.MINUTE_IN_MILLIS

class LogReader(private val logProvider: LogFileProvider) {

    private val mapper = LogMapper()

    fun readLogsFromLast(minutes: Int): List<LogItem> =
        logProvider.logFile()
            .takeIf { it.exists() }
            ?.useLines { sequence ->
                val startTime = System.currentTimeMillis() - minutes * MINUTE_IN_MILLIS
                sequence.filter { line ->
                    mapper.mapDate(line)
                        ?.let { it.time >= startTime } ?: false
                }
                    .mapNotNull { mapper.map(it) }
                    .toList()
            } ?: emptyList()
}
