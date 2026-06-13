package com.manish.wordhaven.presentation.gameplay

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.res.painterResource
import com.manish.wordhaven.R
import com.manish.wordhaven.presentation.components.CrosswordGrid
import com.manish.wordhaven.presentation.components.LetterWheel
import com.manish.wordhaven.presentation.components.PauseDialog
import com.manish.wordhaven.presentation.components.GameToolbar
import com.manish.wordhaven.presentation.theme.Primary
import com.manish.wordhaven.presentation.theme.Secondary

@Composable
fun GameplayScreen(
    onBack:() -> Unit,
    onPauseClick: () -> Unit,
    onLevelComplete: (Int, Int) -> Unit,
    viewModel: GameplayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val shakeAnim = remember { Animatable(0f) }
    var showPauseDialog by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(uiState.errorTrigger) {
        if (uiState.errorTrigger > 0) {
            shakeAnim.animateTo(
                targetValue = 15f,
                animationSpec = repeatable(
                    iterations = 3,
                    animation = tween(durationMillis = 50, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            shakeAnim.animateTo(0f)
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
    uiState.level?.let { level ->
        val onLevelCompleteCalled = remember { mutableStateOf(false) }
        LaunchedEffect(uiState.isLevelComplete) {
            if (uiState.isLevelComplete && !onLevelCompleteCalled.value) {
                onLevelCompleteCalled.value = true
                onLevelComplete(level.id, 50)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
                Image(
                    painter = painterResource(R.drawable.ic_game_bg),
                    contentDescription = "game bg",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.8f
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar
                    GameToolbar(
                        title = "Level ${level.id}",
                        onNavigationClick = onBack,
                        navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                        onActionIconClick = { showPauseDialog = true },
                        actionIcon = Icons.Filled.Pause,
                        isAction = true
                    )

                    if (isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Grid on left
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .graphicsLayer(translationX = shakeAnim.value),
                                contentAlignment = Alignment.Center
                            ) {
                                CrosswordGrid(
                                    level = level,
                                    foundWords = uiState.foundWords
                                )
                            }

                            // Letter Wheel on right
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                LetterWheel(
                                    letters = level.letters,
                                    onWordSelected = { word ->
                                        viewModel.onWordSubmitted(word)
                                    }
                                )
                            }
                        }
                    } else {
                        // Portrait Layout
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .graphicsLayer(translationX = shakeAnim.value),
                            contentAlignment = Alignment.Center
                        ) {
                            CrosswordGrid(
                                level = level,
                                foundWords = uiState.foundWords
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LetterWheel(
                                letters = level.letters,
                                onWordSelected = { word ->
                                    viewModel.onWordSubmitted(word)
                                },
                            )
                        }
                    }
                }

                if (showPauseDialog) {
                    PauseDialog(
                        onDismiss = { showPauseDialog = false },
                        onResume = { showPauseDialog = false },
                        onQuit = onPauseClick
                    )
                }
            }
        }
    }
}
