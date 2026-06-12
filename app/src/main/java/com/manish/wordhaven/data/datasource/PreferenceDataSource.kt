package com.manish.wordhaven.data.datasource

import com.manish.wordhaven.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface PreferenceDataSource {
    val userProgress: Flow<UserProgress>
    suspend fun updateCurrentLevel(levelId: Int)
    suspend fun updateUnlockedLevels(levelId: Int)
    suspend fun updateCoins(coins: Int)
    suspend fun addCompletedLevel(levelId: Int)
}
