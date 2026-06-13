package com.manish.wordhaven.domain.model

data class UserProgress(
    val currentLevel: Int = 1,
    val unlockedLevels: Int = 1,
    val completedLevels: Set<Int> = emptySet()
)
