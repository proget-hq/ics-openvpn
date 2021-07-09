package pl.enterprise.openvpn.data

import de.blinkt.openvpn.core.VpnStatus

data class Config(
    val allowDisconnect: Boolean,
    val logs: Logs?
) {
    constructor() : this(true, null)
}

data class Logs(
    val keepLogsInDays: Int,
    val logLevels: List<VpnStatus.LogLevel>
)
