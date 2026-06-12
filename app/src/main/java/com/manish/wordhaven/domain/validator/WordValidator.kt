package com.manish.wordhaven.domain.validator

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordValidator @Inject constructor() {

    fun isValidWord(word: String, targetWords: List<String>): Boolean {
        return targetWords.contains(word.uppercase())
    }

    fun isBonusWord(word: String, targetWords: List<String>, bonusWords: List<String>): Boolean {
        val upperWord = word.uppercase()
        Log.e("TAG", "onWordSubmitted  isBonusWord: $upperWord", )
        return !targetWords.contains(upperWord) && bonusWords.contains(upperWord)
    }
}
