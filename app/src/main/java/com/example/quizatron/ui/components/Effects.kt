package com.example.quizatron.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import com.example.quizatron.ui.theme.QuizBlue
import com.example.quizatron.ui.theme.QuizGreen
import com.example.quizatron.ui.theme.QuizPink
import com.example.quizatron.ui.theme.QuizYellow
import kotlin.math.PI
import kotlin.math.sin

private data class Bubble(val x: Float, val y: Float, val radius: Float, val alpha: Float, val seed: Float)

private val Bubbles = listOf(
    Bubble(0.12f, 0.10f, 0.12f, 0.20f, 0.00f),
    Bubble(0.90f, 0.18f, 0.08f, 0.18f, 0.30f),
    Bubble(0.82f, 0.55f, 0.16f, 0.14f, 0.60f),
    Bubble(0.08f, 0.70f, 0.10f, 0.18f, 0.15f),
    Bubble(0.55f, 0.93f, 0.14f, 0.16f, 0.45f),
    Bubble(0.30f, 0.40f, 0.05f, 0.20f, 0.80f),
)

/** Fundo em degradê com bolhas que flutuam devagar. */
@Composable
fun BubbleBackground(
    base: Color,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "bolhas")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16000, easing = LinearEasing), RepeatMode.Restart),
        label = "fase",
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(lerp(base, Color.White, 0.35f), base)))
            .drawBehind {
                Bubbles.forEach { b ->
                    val dy = sin((phase + b.seed) * 2f * PI.toFloat()) * size.height * 0.02f
                    drawCircle(
                        color = Color.White.copy(alpha = b.alpha),
                        radius = size.minDimension * b.radius,
                        center = Offset(size.width * b.x, size.height * b.y + dy),
                    )
                }
            },
        content = content,
    )
}

/** Confete caindo, para comemorar quando o jogador acerta tudo. Não captura toques. */
@Composable
fun Confetti(modifier: Modifier = Modifier, count: Int = 40) {
    val transition = rememberInfiniteTransition(label = "confete")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Restart),
        label = "queda",
    )
    val colors = remember { listOf(QuizBlue, QuizPink, QuizYellow, QuizGreen) }

    Canvas(modifier.fillMaxSize()) {
        for (i in 0 until count) {
            val seed = (i * 0.618034f) % 1f
            val x = size.width * ((i * 0.137f + 0.05f) % 1f) + sin((phase + seed) * 4f * PI.toFloat()) * 18f
            val y = ((phase + seed) % 1f) * (size.height + 60f) - 30f
            rotate(degrees = phase * 720f + i * 40f, pivot = Offset(x, y)) {
                drawRect(
                    color = colors[i % colors.size],
                    topLeft = Offset(x - 7f, y - 3.5f),
                    size = Size(14f, 7f),
                )
            }
        }
    }
}
