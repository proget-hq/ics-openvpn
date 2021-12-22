package pl.enterprise.openvpn

import java.text.SimpleDateFormat
import java.util.Locale

object CalendarApi {

    fun dateFormatter() =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
}
