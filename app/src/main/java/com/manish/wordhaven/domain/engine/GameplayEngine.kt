package com.manish.wordhaven.domain.engine

import com.manish.wordhaven.domain.model.Level
import com.manish.wordhaven.domain.validator.WordValidator
import javax.inject.Inject

class GameplayEngine @Inject constructor(
    private val validator: WordValidator
) {
    fun submitWord(
        word: String,
        level: Level,
        foundWords: Set<String>
    ): WordResult {
        val upperWord = word.uppercase()
        
        if (foundWords.contains(upperWord)) {
            return WordResult.Duplicate
        }

        // Check if it's a grid word
        if (validator.isValidWord(upperWord, level.words)) {
            val updatedFoundWords = foundWords + upperWord
            val isComplete = updatedFoundWords.size == level.words.size
            return WordResult.Valid(isComplete)
        }

        // Check if it's a bonus word
        if (validator.isBonusWord(upperWord, level.words, level.bonusWords)) {
            return WordResult.Bonus
        }

        return WordResult.Invalid
    }
}

sealed class WordResult {
    data class Valid(val isLevelComplete: Boolean) : WordResult()
    object Bonus : WordResult()
    object Invalid : WordResult()
    object Duplicate : WordResult()
}
