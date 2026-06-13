package com.manish.wordhaven.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manish.wordhaven.presentation.theme.Primary
import kotlin.math.*

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalTextApi::class)
@Composable
fun LetterWheel(
    letters: List<String>,
    onWordSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndices by remember { mutableStateOf(emptyList<Int>()) }
    var currentTouchPoint by remember { mutableStateOf<Offset?>(null) }
    val textMeasurer = rememberTextMeasurer()

    BoxWithConstraints(modifier = modifier) {
        val center = Offset(constraints.maxWidth / 2f, constraints.maxHeight / 2f)
        val radius = min(constraints.maxWidth, constraints.maxHeight) / 3.8f
        val nodeRadius = 36.dp.value * 2f

        val nodes = remember(letters) {
            val angleStep = 2 * PI / letters.size
            letters.mapIndexed { index, letter ->
                val angle = index * angleStep - PI / 2
                LetterNode(
                    letter = letter,
                    center = Offset(
                        (center.x + radius * cos(angle)).toFloat(),
                        (center.y + radius * sin(angle)).toFloat()
                    )
                )
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(letters) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val index = nodes.indexOfFirst { (it.center - offset).getDistance() < nodeRadius }
                            if (index != -1) {
                                selectedIndices = listOf(index)
                                currentTouchPoint = offset
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentTouchPoint = change.position
                            val index = nodes.indexOfFirst { (it.center - change.position).getDistance() < nodeRadius }
                            if (index != -1 && !selectedIndices.contains(index)) {
                                selectedIndices = selectedIndices + index
                            }
                        },
                        onDragEnd = {
                            if (selectedIndices.isNotEmpty()) {
                                val word = selectedIndices.joinToString("") { nodes[it].letter }
                                onWordSelected(word)
                            }
                            selectedIndices = emptyList()
                            currentTouchPoint = null
                        },
                        onDragCancel = {
                            selectedIndices = emptyList()
                            currentTouchPoint = null
                        }
                    )
                }
        ) {
            // Draw large wheel background with padding
            drawCircle(
                color = Color.White.copy(alpha = 0.5f),
                radius = radius + nodeRadius * 1.6f,
                center = center
            )

            // Draw connection lines
            if (selectedIndices.isNotEmpty()) {
                for (i in 0 until selectedIndices.size - 1) {
                    drawLine(
                        color = Primary.copy(alpha = 0.8f),
                        start = nodes[selectedIndices[i]].center,
                        end = nodes[selectedIndices[i + 1]].center,
                        strokeWidth = 20f,
                        cap = StrokeCap.Round
                    )
                }
                currentTouchPoint?.let { touchPoint ->
                    drawLine(
                        color = Primary.copy(alpha = 0.5f),
                        start = nodes[selectedIndices.last()].center,
                        end = touchPoint,
                        strokeWidth = 20f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // Draw nodes
            nodes.forEachIndexed { index, node ->
                val isSelected = selectedIndices.contains(index)
                
                // Background circle with "padding" effect
                drawCircle(
                    color = if (isSelected) Primary else Color.White,
                    radius = nodeRadius,
                    center = node.center
                )
                
                if (!isSelected) {
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        radius = nodeRadius,
                        center = node.center,
                        style = Stroke(width = 1f)
                    )
                }

                val textLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(node.letter),
                    style = TextStyle(
                        color = if (isSelected) Color.White else Primary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(
                        node.center.x - textLayoutResult.size.width / 2,
                        node.center.y - textLayoutResult.size.height / 2
                    )
                )
            }
        }
    }
}

data class LetterNode(
    val letter: String,
    val center: Offset
)
