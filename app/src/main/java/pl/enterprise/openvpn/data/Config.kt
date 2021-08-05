package pl.enterprise.openvpn.data

import android.os.Bundle
import de.blinkt.openvpn.core.VpnStatus

data class Config(
    val autoConnect: Boolean,
    val allowDisconnect: Boolean,
    val logs: Logs?,
    val allowImportProfile: Boolean
) {

    constructor() : this(
        autoConnect = false,
        allowDisconnect = true,
        logs = null,
        allowImportProfile = false
    )

    constructor(bundle: Bundle) : this(
        autoConnect = bundle.getBoolean("autoConnect", false),
        allowDisconnect = bundle.getBoolean("allowUserDisconnect", true),
        logs = bundle.getBundle("logs")?.run {
            Logs(
                keepLogsInDays = getInt("keepLogsDays", 1),
                logLevels = getStringArray("logLevel")?.mapNotNull {
                    VpnStatus.LogLevel.getEnumByValue(it.toInt())
                } ?: emptyList()
            )
        } ?: Logs(),
        allowImportProfile = bundle.getBoolean("allowImportProfileFromOvpnFile", false)
    )
}

data class Logs(
    val keepLogsInDays: Int,
    val logLevels: List<VpnStatus.LogLevel>
) {
    constructor() : this(1, VpnStatus.LogLevel.values().toList())
}
