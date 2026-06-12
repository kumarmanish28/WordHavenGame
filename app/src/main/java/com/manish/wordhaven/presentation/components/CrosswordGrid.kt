package com.manish.wordhaven.presentation.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.wordhaven.domain.model.GridWord
import com.manish.wordhaven.domain.model.Level
import com.manish.wordhaven.presentation.theme.Primary

@Composable
fun CrosswordGrid(
    level: Level,
    foundWords: Set<String>,
    modifier: Modifier = Modifier
) {
    val maxRow = level.gridWords.maxOf { if (it.isVertical) it.row + it.word.length - 1 else it.row }
    val maxCol = level.gridWords.maxOf { if (it.isVertical) it.col else it.col + it.word.length - 1 }

    val grid = Array(maxRow + 1) { arrayOfNulls<Char>(maxCol + 1) }
    level.gridWords.forEach { gridWord ->
        gridWord.word.forEachIndexed { index, char ->
            if (gridWord.isVertical) {
                grid[gridWord.row + index][gridWord.col] = char
            } else {
                grid[gridWord.row][gridWord.col + index] = char
            }
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        for (r in 0..maxRow) {
            Row {
                for (c in 0..maxCol) {
                    val char = grid[r][c]
                    val isPartOfFoundWord = level.gridWords.any { gw ->
                        foundWords.contains(gw.word) && isCellInWord(r, c, gw)
                    }

                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (char != null) Color.White.copy(alpha = 0.9f) else Color.Transparent)
                            .border(
                                width = if (char != null) 1.dp else 0.dp,
                                color = if (char != null) Color.LightGray else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (char != null && isPartOfFoundWord) {
                            Text(
                                text = char.toString(),
                                fontSize = 20.sp,
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

private fun isCellInWord(row: Int, col: Int, gridWord: GridWord): Boolean {
    return if (gridWord.isVertical) {
        col == gridWord.col && row in gridWord.row until (gridWord.row + gridWord.word.length)
    } else {
        row == gridWord.row && col in gridWord.col until (gridWord.col + gridWord.word.length)
    }
}
