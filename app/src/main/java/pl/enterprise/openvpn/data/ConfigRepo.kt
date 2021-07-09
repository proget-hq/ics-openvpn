package pl.enterprise.openvpn.data

import android.content.Context
import android.content.RestrictionsManager
import de.blinkt.openvpn.core.VpnStatus

class ConfigRepo private constructor(
    restrictionsManager: RestrictionsManager
) {
    private var config: Config = restrictionsManager.applicationRestrictions?.run {
        Config(
            allowDisconnect = getBoolean("allow_disconnect", true),
            logs = getBundle("logs")?.run {
                Logs(
                    keepLogsInDays = getInt("keepLogsDays", 1),
                    logLevels = getStringArray("logLevel")?.mapNotNull {
                        VpnStatus.LogLevel.getEnumByValue(it.toInt())
                    } ?: emptyList()
                )
            }
        )
    } ?: Config()

    fun fetchConfig(): Config =
        config

    fun updateConfig(config: Config) {
        this.config = config
    }

    companion object {
        private var instance: ConfigRepo? = null

        fun getInstance(context: Context): ConfigRepo =
            instance
                ?: ConfigRepo(context.getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager)
                    .also { instance = it }
    }
}
