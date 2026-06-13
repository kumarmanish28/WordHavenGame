package com.manish.wordhaven.data.repository

import com.manish.wordhaven.domain.model.Level
import com.manish.wordhaven.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    suspend fun getLevels(): List<Level>
    suspend fun getLevelById(id: Int): Level?
    fun getUserProgress(): Flow<UserProgress>
    suspend fun updateCurrentLevel(levelId: Int)
    suspend fun completeLevel(levelId: Int)
}
