package com.manish.wordhaven.presentation.levelselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manish.wordhaven.data.repository.GameRepository
import com.manish.wordhaven.domain.model.UserProgress
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LevelSelectViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LevelSelectUiState())
    val uiState: StateFlow<LevelSelectUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getUserProgress().collect { progress ->
                _uiState.update { it.copy(userProgress = progress) }
            }
        }
    }
}

data class LevelSelectUiState(
    val userProgress: UserProgress = UserProgress()
)
