package com.manish.wordhaven.presentation.gameplay

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.wordhaven.data.repository.GameRepository
import com.manish.wordhaven.domain.engine.GameplayEngine
import com.manish.wordhaven.domain.engine.WordResult
import com.manish.wordhaven.domain.model.Level
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameplayViewModel @Inject constructor(
    private val repository: GameRepository,
    private val engine: GameplayEngine,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val levelId: Int = checkNotNull(savedStateHandle["levelId"])

    private val _uiState = MutableStateFlow(GameplayUiState())
    val uiState: StateFlow<GameplayUiState> = _uiState.asStateFlow()

    init {
        loadLevel()
    }

    private fun loadLevel() {
        viewModelScope.launch {
            val level = repository.getLevelById(levelId)
            if (level != null) {
                _uiState.update { it.copy(level = level, isLoading = false) }
            }
        }
    }

    fun onWordSubmitted(word: String) {
        val currentState = _uiState.value
        val level = currentState.level ?: return

        val result = engine.submitWord(word, level, currentState.foundWords)
        
        when (result) {
            is WordResult.Valid -> {
                val newFoundWords = currentState.foundWords + word.uppercase()
                _uiState.update { 
                    it.copy(
                        foundWords = newFoundWords,
                        isLevelComplete = result.isLevelComplete,
                        lastWordResult = WordSubmissionResult.SUCCESS,
                        lastSubmittedWord = word.uppercase()
                    )
                }
                if (result.isLevelComplete) {
                    completeLevel()
                }
            }
            is WordResult.Bonus -> {
                // Bonus word found
            }
            WordResult.Invalid, WordResult.Duplicate -> {
                _uiState.update { 
                    it.copy(
                        lastWordResult = WordSubmissionResult.FAILURE,
                        lastSubmittedWord = word.uppercase(),
                        errorTrigger = it.errorTrigger + 1 
                    )
                }
            }
        }
    }

    fun clearSubmissionResult() {
        _uiState.update { it.copy(lastWordResult = null, lastSubmittedWord = null) }
    }

    private fun completeLevel() {
        viewModelScope.launch {
            repository.completeLevel(levelId)
        }
    }
}

enum class WordSubmissionResult {
    SUCCESS, FAILURE
}

data class GameplayUiState(
    val level: Level? = null,
    val foundWords: Set<String> = emptySet(),
    val isLoading: Boolean = true,
    val isLevelComplete: Boolean = false,
    val errorTrigger: Int = 0,
    val lastWordResult: WordSubmissionResult? = null,
    val lastSubmittedWord: String? = null
)
