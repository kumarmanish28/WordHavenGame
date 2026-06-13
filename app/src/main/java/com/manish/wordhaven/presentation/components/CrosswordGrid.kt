package com.manish.wordhaven.presentation.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.wordhaven.domain.model.GridWord
import com.manish.wordhaven.domain.model.Level
import com.manish.wordhaven.presentation.gameplay.WordSubmissionResult
import com.manish.wordhaven.presentation.theme.Primary
import kotlinx.coroutines.delay

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CrosswordGrid(
    level: Level,
    foundWords: Set<String>,
    lastSubmittedWord: String? = null,
    lastWordResult: WordSubmissionResult? = null,
    modifier: Modifier = Modifier
) {
    val maxRow = level.gridWords.maxOf { if (it.isVertical) it.row + it.word.length - 1 else it.row }
    val maxCol = level.gridWords.maxOf { if (it.isVertical) it.col else it.col + it.word.length - 1 }
    val rowCount = maxRow + 1
    val colCount = maxCol + 1

    val grid = Array(rowCount) { arrayOfNulls<Char>(colCount) }
    level.gridWords.forEach { gridWord ->
        gridWord.word.forEachIndexed { index, char ->
            if (gridWord.isVertical) {
                grid[gridWord.row + index][gridWord.col] = char
            } else {
                grid[gridWord.row][gridWord.col + index] = char
            }
        }
    }

    BoxWithConstraints(modifier = modifier.padding(4.dp)) {
        val density = LocalDensity.current
        val availableWidthPx = constraints.maxWidth
        val availableHeightPx = constraints.maxHeight
        
        val maxCellWidthPx = availableWidthPx / colCount
        val maxCellHeightPx = availableHeightPx / rowCount
        
        val defaultCellSizePx = with(density) { 72.dp.toPx() }
        val cellSizePx = minOf(maxCellWidthPx.toFloat(), maxCellHeightPx.toFloat(), defaultCellSizePx)
        val cellSizeDp = with(density) { cellSizePx.toDp() }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (r in 0 until rowCount) {
                Row {
                    for (c in 0 until colCount) {
                        val char = grid[r][c]
                        
                        val gridWord = level.gridWords.find { gw ->
                            foundWords.contains(gw.word) && isCellInWord(r, c, gw)
                        }
                        val isPartOfFoundWord = gridWord != null
                        
                        val isNewWord = gridWord?.word == lastSubmittedWord && lastWordResult == WordSubmissionResult.SUCCESS
                        val indexInWord = if (gridWord != null) {
                            if (gridWord.isVertical) r - gridWord.row else c - gridWord.col
                        } else 0

                        val infiniteTransition = rememberInfiniteTransition(label = "dance")
                        val danceOffset by infiniteTransition.animateFloat(
                            initialValue = -3f,
                            targetValue = 3f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "danceOffset"
                        )
                        
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = -2f,
                            targetValue = 2f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1500, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "danceRotation"
                        )

                        var visible by remember { mutableStateOf(false) }
                        
                        LaunchedEffect(isPartOfFoundWord) {
                            if (isPartOfFoundWord) {
                                if (isNewWord) {
                                    delay(indexInWord * 100L) // Staggered reveal
                                }
                                visible = true
                            } else {
                                visible = false
                            }
                        }

                        val alpha by animateFloatAsState(
                            targetValue = if (visible) 1f else 0f,
                            animationSpec = tween(500)
                        )
                        
                        val scale by animateFloatAsState(
                            targetValue = if (visible) 1f else 0.8f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )

                        Box(
                            modifier = Modifier
                                .size(cellSizeDp)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    when {
                                        char != null && isPartOfFoundWord -> Color.White
                                        char != null -> Color.White.copy(alpha = 0.25f)
                                        else -> Color.Transparent
                                    }
                                )
                                .border(
                                    width = if (char != null) 2.dp else 0.dp,
                                    color = if (char != null && isPartOfFoundWord) Color.White else if (char != null) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (char != null) {
                                Text(
                                    text = char.toString(),
                                    fontSize = (cellSizeDp.value * 0.45f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .graphicsLayer {
                                            this.alpha = alpha
                                            this.scaleX = scale
                                            this.scaleY = scale
                                            if (isPartOfFoundWord) {
                                                this.translationY = danceOffset
                                                this.rotationZ = rotation
                                            }
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun isCellInWord(row: Int, col: Int, gridWord: GridWord): Boolean {
    return if (gridWord.isVertical) {
        col == gridWord.col && row in gridWord.row until (gridWord.row + gridWord.word.length)
    } else {
        row == gridWord.row && col in gridWord.col until (gridWord.col + gridWord.word.length)
    }
}
