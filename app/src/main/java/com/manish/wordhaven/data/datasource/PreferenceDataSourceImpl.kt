package com.manish.wordhaven.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.manish.wordhaven.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferenceDataSource {

    private object PreferencesKeys {
        val CURRENT_LEVEL = intPreferencesKey("current_level")
        val UNLOCKED_LEVELS = intPreferencesKey("unlocked_levels")
        val COINS = intPreferencesKey("coins")
        val COMPLETED_LEVELS = stringSetPreferencesKey("completed_levels")
    }

    override val userProgress: Flow<UserProgress> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserProgress(
                currentLevel = preferences[PreferencesKeys.CURRENT_LEVEL] ?: 1,
                unlockedLevels = preferences[PreferencesKeys.UNLOCKED_LEVELS] ?: 1,
                coins = preferences[PreferencesKeys.COINS] ?: 0,
                completedLevels = preferences[PreferencesKeys.COMPLETED_LEVELS]?.map { it.toInt() }?.toSet() ?: emptySet()
            )
        }

    override suspend fun updateCurrentLevel(levelId: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENT_LEVEL] = levelId
        }
    }

    override suspend fun updateUnlockedLevels(levelId: Int) {
        dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.UNLOCKED_LEVELS] ?: 1
            if (levelId > current) {
                preferences[PreferencesKeys.UNLOCKED_LEVELS] = levelId
            }
        }
    }

    override suspend fun updateCoins(coins: Int) {
        dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.COINS] ?: 0
            preferences[PreferencesKeys.COINS] = current + coins
        }
    }

    override suspend fun addCompletedLevel(levelId: Int) {
        dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.COMPLETED_LEVELS] ?: emptySet()
            preferences[PreferencesKeys.COMPLETED_LEVELS] = current + levelId.toString()
        }
    }
}
