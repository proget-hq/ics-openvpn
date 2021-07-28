package pl.enterprise.openvpn.data

import android.os.Bundle
import de.blinkt.openvpn.core.VpnStatus

data class Config(
    val allowDisconnect: Boolean,
    val logs: Logs?,
    val allowImportProfile: Boolean
) {

    constructor() : this(true, null, false)
    constructor(bundle: Bundle) : this(
        allowDisconnect = bundle.getBoolean("allowUserDisconnect", true),
        logs = bundle.getBundle("logs")?.run {
            Logs(
                keepLogsInDays = getInt("keepLogsDays", 1),
                logLevels = getStringArray("logLevel")?.mapNotNull {
                    VpnStatus.LogLevel.getEnumByValue(it.toInt())
                } ?: emptyList()
            )
        },
        allowImportProfile = bundle.getBoolean("allowImportProfileFromOvpnFile", false)
    )
}

data class Logs(
    val keepLogsInDays: Int,
    val logLevels: List<VpnStatus.LogLevel>
)
