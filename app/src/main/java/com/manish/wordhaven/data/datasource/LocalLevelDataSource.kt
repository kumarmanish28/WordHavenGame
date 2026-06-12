package com.manish.wordhaven.data.datasource

import android.content.Context
import com.manish.wordhaven.domain.model.Level
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalLevelDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) : LevelDataSource {

    private var cachedLevels: List<Level>? = null

    override suspend fun getLevels(): List<Level> = withContext(Dispatchers.IO) {
        cachedLevels ?: loadLevelsFromAssets().also { cachedLevels = it }
    }

    override suspend fun getLevelById(id: Int): Level? {
        return getLevels().find { it.id == id }
    }

    private fun loadLevelsFromAssets(): List<Level> {
        return try {
            val jsonString = context.assets.open("levels.json").bufferedReader().use { it.readText() }
            Json.decodeFromString<List<Level>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
