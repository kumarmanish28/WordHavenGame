package com.manish.wordhaven.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
        val density = androidx.compose.ui.platform.LocalDensity.current
        val center = Offset(constraints.maxWidth / 2f, constraints.maxHeight / 2f)
        val radius = min(constraints.maxWidth, constraints.maxHeight) / 3.8f
        val nodeRadius = with(density) { 32.dp.toPx() }

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
            // 1. Draw large wheel background (Glass effect)
            drawCircle(
                color = Color.White.copy(alpha = 0.2f),
                radius = radius + nodeRadius * 1.5f,
                center = center
            )

            // 2. Draw connection lines
            if (selectedIndices.isNotEmpty()) {
                for (i in 0 until selectedIndices.size - 1) {
                    drawLine(
                        color = Primary,
                        start = nodes[selectedIndices[i]].center,
                        end = nodes[selectedIndices[i + 1]].center,
                        strokeWidth = 24f, // Thicker lines
                        cap = StrokeCap.Round
                    )
                }
                currentTouchPoint?.let { touchPoint ->
                    drawLine(
                        color = Primary.copy(alpha = 0.6f),
                        start = nodes[selectedIndices.last()].center,
                        end = touchPoint,
                        strokeWidth = 24f,
                        cap = StrokeCap.Round
                    )
                }
            }

            // 3. Draw nodes
            nodes.forEachIndexed { index, node ->
                val isSelected = selectedIndices.contains(index)
                
                // Outer glow/border for selected nodes
                if (isSelected) {
                    drawCircle(
                        color = Primary.copy(alpha = 0.3f),
                        radius = nodeRadius + 4.dp.toPx(),
                        center = node.center
                    )
                }

                // Main circle
                drawCircle(
                    color = if (isSelected) Primary else Color.White,
                    radius = nodeRadius,
                    center = node.center
                )
                
                val textLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(node.letter),
                    style = TextStyle(
                        color = if (isSelected) Color.White else Color(0xFF333333),
                        fontSize = 30.sp,
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

            // 4. Draw current word bubble at the top
            if (selectedIndices.isNotEmpty()) {
                val currentWord = selectedIndices.joinToString("") { nodes[it].letter }
                val wordTextResult = textMeasurer.measure(
                    text = AnnotatedString(currentWord),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                
                val paddingH = 28.dp.toPx()
                val paddingV = 12.dp.toPx()
                val bubbleWidth = max(wordTextResult.size.width + paddingH * 2, 80.dp.toPx())
                val bubbleHeight = wordTextResult.size.height + paddingV * 2
                val bubbleTop = center.y - radius - nodeRadius - 80.dp.toPx()
                
                // Draw Primary Capsule
                drawRoundRect(
                    color = Primary,
                    topLeft = Offset(center.x - bubbleWidth / 2, bubbleTop),
                    size = Size(bubbleWidth, bubbleHeight),
                    cornerRadius = CornerRadius(bubbleHeight / 2, bubbleHeight / 2)
                )
                
                // Draw Capsule Border
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.5f),
                    topLeft = Offset(center.x - bubbleWidth / 2, bubbleTop),
                    size = Size(bubbleWidth, bubbleHeight),
                    cornerRadius = CornerRadius(bubbleHeight / 2, bubbleHeight / 2),
                    style = Stroke(width = 2.dp.toPx())
                )

                drawText(
                    textLayoutResult = wordTextResult,
                    topLeft = Offset(
                        center.x - wordTextResult.size.width / 2,
                        bubbleTop + (bubbleHeight - wordTextResult.size.height) / 2
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
