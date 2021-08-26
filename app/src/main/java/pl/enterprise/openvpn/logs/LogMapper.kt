package pl.enterprise.openvpn.logs

import pl.enterprise.openvpn.CalendarApi.dateFormatter
import java.util.Date

class LogMapper {
    fun mapDate(line: String): Date? =
        try {
            line.substring(0, 18).let {
                dateFormatter().parse(it)
            }
        } catch (e: Exception) {
            null
        }

    fun map(line: String): LogItem? =
        try {
            LogItem(mapDate(line)!!, line.substring(19))
        } catch (e: Exception) {
            null
        }
}
