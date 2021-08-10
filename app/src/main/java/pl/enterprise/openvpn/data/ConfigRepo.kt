package pl.enterprise.openvpn.data

import android.content.Context
import android.content.RestrictionsManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConfigRepo private constructor(
    restrictionsManager: RestrictionsManager,
    private val dataStore: DataStore<Preferences>
) {
    private val appRestrictionHash = stringPreferencesKey(APP_RESTRICTIONS_HASH_KEY)
    private var config: Config = restrictionsManager.applicationRestrictions
        ?.run { Config(this) }
        ?: Config()

    fun fetchConfig(): Config =
        config

    fun updateConfig(config: Config) {
        this.config = config
    }

    fun fetchLastAppRestrictionsHash(): Flow<String?> =
        dataStore.data.map { it[appRestrictionHash] }

    suspend fun insertAppRestrictionsHash(hash: String) {
        dataStore.edit {
            it[appRestrictionHash] = hash
        }
    }

    companion object {
        private var instance: ConfigRepo? = null

        fun getInstance(context: Context): ConfigRepo =
            instance
                ?: ConfigRepo(
                    context.getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager,
                    context.dataStore
                )
                    .also { instance = it }

        private const val APP_RESTRICTIONS_HASH_KEY = "app_restrictions_hash_key"
    }
}
