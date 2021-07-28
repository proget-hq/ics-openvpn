package pl.enterprise.openvpn.data

import android.content.Context
import android.content.RestrictionsManager

class ConfigRepo private constructor(
    restrictionsManager: RestrictionsManager
) {
    private var config: Config = restrictionsManager.applicationRestrictions
        ?.run { Config(this) }
        ?: Config()

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
