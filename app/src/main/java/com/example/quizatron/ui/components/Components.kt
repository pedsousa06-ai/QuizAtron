package com.example.quizatron.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizatron.ui.theme.Ink
import com.example.quizatron.ui.theme.QuizBlue
import com.example.quizatron.ui.theme.QuizGreen
import com.example.quizatron.ui.theme.QuizPink
import com.example.quizatron.ui.theme.QuizYellow
import kotlin.math.PI
import kotlin.math.sin

/**
 * Logo desenhado em código (quatro balões Q-U-I-Z).
 * Com [animated] = true os balões balançam suavemente, um de cada vez.
 */
@Composable
fun QuizLogo(modifier: Modifier = Modifier, size: Dp = 120.dp, animated: Boolean = false) {
    val phase = if (animated) {
        val transition = rememberInfiniteTransition(label = "logo")
        val p by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing), RepeatMode.Restart),
            label = "flutuar",
        )
        p
    } else {
        0f
    }

    val letters = listOf("Q" to QuizBlue, "U" to QuizYellow, "I" to QuizPink, "Z" to QuizGreen)
    val circle = size * 0.4f
    val step = size * 0.2f

    Box(modifier.size(size).semantics { contentDescription = "Logo do Quiz" }) {
        letters.forEachIndexed { i, (letter, color) ->
            val bob = if (animated) sin((phase + i * 0.12f) * 2f * PI.toFloat()) * size.value * 0.025f else 0f
            Box(
                modifier = Modifier
                    .offset(x = step * i, y = step * i + bob.dp)
                    .size(circle)
                    .clip(CircleShape)
                    .background(color)
                    .border(2.dp, Ink, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = letter,
                    color = Ink,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (circle.value * 0.5f).sp,
                )
            }
        }
    }
}

/** Etiqueta verde arredondada ("Pergunta 1 de 3", "Bom trabalho!"). */
@Composable
fun PillLabel(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = QuizGreen,
        border = BorderStroke(1.5.dp, Ink),
        shadowElevation = 4.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
            color = Ink,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

/** Botão amarelo em formato de pílula, com sombra e ícone opcional. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(58.dp),
        enabled = enabled,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.5.dp, Ink),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 1.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = QuizYellow,
            contentColor = Ink,
            disabledContainerColor = QuizYellow.copy(alpha = 0.5f),
            disabledContentColor = Ink.copy(alpha = 0.5f),
        ),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
