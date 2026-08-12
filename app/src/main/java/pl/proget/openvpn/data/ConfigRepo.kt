package pl.proget.openvpn.data

import android.content.Context
import android.content.RestrictionsManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

class ConfigRepo private constructor(
    private val restrictionsManager: RestrictionsManager,
    private val dataStore: DataStore<Preferences>
) {
    private val appRestrictionHash = stringPreferencesKey(APP_RESTRICTIONS_HASH_KEY)
    private var config: Config = readConfiguration()

    private val _changes = MutableSharedFlow<Config>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val changes: Flow<Config> = _changes.asSharedFlow()

    fun fetchConfig(): Config =
        config

    fun updateConfig(config: Config) {
        this.config = config
        _changes.tryEmit(config)
    }

    fun synchronizeConfiguration() {
        updateConfig(readConfiguration())
    }

    fun fetchLastAppRestrictionsHash(): Flow<String?> =
        dataStore.data.map { it[appRestrictionHash] }

    suspend fun insertAppRestrictionsHash(hash: String) {
        dataStore.edit {
            it[appRestrictionHash] = hash
        }
    }

    private fun readConfiguration(): Config =
        restrictionsManager.applicationRestrictions
            ?.run { Config(this) }
            ?: Config()

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
