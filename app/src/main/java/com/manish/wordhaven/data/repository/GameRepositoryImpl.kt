package com.manish.wordhaven.data.repository

import com.manish.wordhaven.data.datasource.LevelDataSource
import com.manish.wordhaven.data.datasource.PreferenceDataSource
import com.manish.wordhaven.domain.model.Level
import com.manish.wordhaven.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepositoryImpl @Inject constructor(
    private val levelDataSource: LevelDataSource,
    private val preferenceDataSource: PreferenceDataSource
) : GameRepository {

    override suspend fun getLevels(): List<Level> = levelDataSource.getLevels()

    override suspend fun getLevelById(id: Int): Level? = levelDataSource.getLevelById(id)

    override fun getUserProgress(): Flow<UserProgress> = preferenceDataSource.userProgress

    override suspend fun updateCurrentLevel(levelId: Int) {
        preferenceDataSource.updateCurrentLevel(levelId)
    }

    override suspend fun completeLevel(levelId: Int) {
        preferenceDataSource.addCompletedLevel(levelId)
        preferenceDataSource.updateUnlockedLevels(levelId + 1)
        preferenceDataSource.updateCurrentLevel(levelId + 1)
    }

    override suspend fun getTotalLevels(): Int = levelDataSource.getLevels().size

    override suspend fun resetProgress() {
        preferenceDataSource.resetProgress()
    }
}
