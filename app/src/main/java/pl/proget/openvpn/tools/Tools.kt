package pl.proget.openvpn.tools

inline fun <T> tryIgnoringException(unit: () -> T): T? {
    return try {
        unit.invoke()
    } catch (ignored: Exception) {
        null
    }
}
