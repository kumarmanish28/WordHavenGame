package com.manish.wordhaven.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Level(
    val id: Int,
    val letters: List<String>,
    val gridWords: List<GridWord>,
    val bonusWords: List<String> = emptyList()
) {
    val words: List<String> get() = gridWords.map { it.word }
}

@Serializable
data class GridWord(
    val word: String,
    val row: Int,
    val col: Int,
    val isVertical: Boolean
)
