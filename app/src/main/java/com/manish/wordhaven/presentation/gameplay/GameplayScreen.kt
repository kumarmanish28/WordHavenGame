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
import com.manish.wordhaven.presentation.components.CrosswordGrid
import com.manish.wordhaven.presentation.components.LetterWheel
import com.manish.wordhaven.presentation.components.PauseDialog
import com.manish.wordhaven.presentation.theme.Primary
import com.manish.wordhaven.presentation.theme.Secondary
import kotlin.math.log

@Composable
fun GameplayScreen(
    onPauseClick: () -> Unit,
    onLevelComplete: (Int, Int) -> Unit,
    viewModel: GameplayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val shakeAnim = remember { Animatable(0f) }
    var showPauseDialog by remember { mutableStateOf(false) }

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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        uiState.level?.let { level ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showPauseDialog = true }) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause", tint = Primary)
                        }
                        Text(
                            text = "Level ${level.id}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Primary
                        )
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = Color.White.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "💰", modifier = Modifier.padding(end = 4.dp))
                                Text(text = "500", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Grid
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

                    // Letter Wheel Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 32.dp),
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

                if (uiState.isLevelComplete) {
                    onLevelComplete(level.id, 50)
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
