package com.manish.wordhaven.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.wordhaven.domain.model.GridWord
import com.manish.wordhaven.domain.model.Level
import com.manish.wordhaven.presentation.theme.Primary

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun CrosswordGrid(
    level: Level,
    foundWords: Set<String>,
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

    BoxWithConstraints(modifier = modifier.padding(16.dp)) {
        val density = LocalDensity.current
        val availableWidthPx = constraints.maxWidth
        val availableHeightPx = constraints.maxHeight
        
        val maxCellWidthPx = availableWidthPx / colCount
        val maxCellHeightPx = availableHeightPx / rowCount
        
        val defaultCellSizePx = with(density) { 45.dp.toPx() }
        val cellSizePx = minOf(maxCellWidthPx.toFloat(), maxCellHeightPx.toFloat(), defaultCellSizePx) * 1.2f
        val cellSizeDp = with(density) { cellSizePx.toDp() }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            for (r in 0 until rowCount) {
                Row {
                    for (c in 0 until colCount) {
                        val char = grid[r][c]
                        val isPartOfFoundWord = level.gridWords.any { gw ->
                            foundWords.contains(gw.word) && isCellInWord(r, c, gw)
                        }

                        Box(
                            modifier = Modifier
                                .size(cellSizeDp)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (char != null) Color.White.copy(alpha = 0.9f) else Color.Transparent)
                                .border(
                                    width = if (char != null) 1.dp else 0.dp,
                                    color = if (char != null) Color.LightGray else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (char != null && isPartOfFoundWord) {
                                Text(
                                    text = char.toString(),
                                    fontSize = (cellSizeDp.value * 0.6f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
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
