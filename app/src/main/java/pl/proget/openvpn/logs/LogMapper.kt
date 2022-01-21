package pl.proget.openvpn.logs

import pl.proget.openvpn.CalendarApi.dateFormatter
import pl.proget.openvpn.tools.tryIgnoringException
import java.util.Date

class LogMapper {
    fun mapDate(line: String): Date? =
        tryIgnoringException {
            line.substring(DATE_START, DATE_END).let {
                dateFormatter().parse(it)
            }
        }

    fun map(line: String): LogItem? =
        tryIgnoringException {
            LogItem(mapDate(line)!!, line.substring(MESSAGE_START))
        }

    companion object {
        private const val DATE_START = 0
        private const val DATE_END = 18
        private const val MESSAGE_START = 19
    }
}
