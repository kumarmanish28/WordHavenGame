package com.manish.wordhaven.presentation.gameplay

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manish.wordhaven.R
import com.manish.wordhaven.presentation.components.CrosswordGrid
import com.manish.wordhaven.presentation.components.GameToolbar
import com.manish.wordhaven.presentation.components.LetterWheel
import com.manish.wordhaven.presentation.components.PauseDialog
import kotlinx.coroutines.delay

@Composable
fun GameplayScreen(
    onBack:() -> Unit,
    onPauseClick: () -> Unit,
    onLevelComplete: (Int) -> Unit,
    onGameComplete: () -> Unit,
    viewModel: GameplayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val shakeAnim = remember { Animatable(0f) }
    var showPauseDialog by remember { mutableStateOf(false) }
    var showGameCompleteDialog by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(uiState.isGameComplete) {
        if (uiState.isGameComplete) {
            showGameCompleteDialog = true
        }
    }

    LaunchedEffect(uiState.lastWordResult) {
        if (uiState.lastWordResult != null) {
            delay(1000)
            viewModel.clearSubmissionResult()
        }
    }

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
            Image(
                painter = painterResource(R.drawable.ic_game_bg),
                contentDescription = "game bg",
                modifier = Modifier
                    .fillMaxSize()
                    .blur(15.dp),
                contentScale = ContentScale.Crop,
            )
            CircularProgressIndicator()
        }
    } else {
    uiState.level?.let { level ->
        val onLevelCompleteCalled = remember { mutableStateOf(false) }
        LaunchedEffect(uiState.isLevelComplete) {
            if (uiState.isLevelComplete && !uiState.isGameComplete && !onLevelCompleteCalled.value) {
                delay(2500)
                onLevelCompleteCalled.value = true
                onLevelComplete(level.id)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
                Image(
                    painter = painterResource(R.drawable.ic_game_bg),
                    contentDescription = "game bg",
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(15.dp),
                    contentScale = ContentScale.Crop,
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
                                    foundWords = uiState.foundWords,
                                    lastSubmittedWord = uiState.lastSubmittedWord,
                                    lastWordResult = uiState.lastWordResult
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
                                foundWords = uiState.foundWords,
                                lastSubmittedWord = uiState.lastSubmittedWord,
                                lastWordResult = uiState.lastWordResult
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

                // Animation for submitted word - REMOVED AS PER USER REQUEST
                /*
                uiState.lastSubmittedWord?.let { word ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        AnimatedVisibility(
                            visible = uiState.lastWordResult != null,
                            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (uiState.lastWordResult == WordSubmissionResult.SUCCESS) Primary else Color.Red)
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = word,
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                */

                if (showPauseDialog) {
                    PauseDialog(
                        onDismiss = { showPauseDialog = false },
                        onResume = { showPauseDialog = false },
                        onQuit = {
                            showPauseDialog = false
                            onPauseClick.invoke()
                        }
                    )
                }

                if (showGameCompleteDialog) {
                    AlertDialog(
                        onDismissRequest = { /* Prevent dismiss */ },
                        title = { Text("Congratulations!") },
                        text = { Text("You have completed all levels in Word Haven! Progress will be reset.") },
                        confirmButton = {
                            Button(onClick = {
                                viewModel.resetGame()
                                showGameCompleteDialog = false
                                onGameComplete()
                            }) {
                                Text("Restart Game")
                            }
                        }
                    )
                }
            }
        }
    }
}
