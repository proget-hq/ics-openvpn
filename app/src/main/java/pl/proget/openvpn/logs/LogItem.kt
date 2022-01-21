package pl.proget.openvpn.logs

import pl.proget.openvpn.CalendarApi.dateFormatter
import java.util.Date

data class LogItem(
    val time: Date,
    val message: String
) {
    override fun toString(): String =
        "${dateFormatter().format(time)}  $message"
}
