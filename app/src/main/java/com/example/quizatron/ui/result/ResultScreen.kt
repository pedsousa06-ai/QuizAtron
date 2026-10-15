package com.example.quizatron.ui.result

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizatron.ui.components.BubbleBackground
import com.example.quizatron.ui.components.Confetti
import com.example.quizatron.ui.components.PillLabel
import com.example.quizatron.ui.components.PrimaryButton
import com.example.quizatron.ui.components.QuizLogo
import com.example.quizatron.ui.quiz.QuizUiState
import com.example.quizatron.ui.theme.Ink
import com.example.quizatron.ui.theme.InkSoft
import com.example.quizatron.ui.theme.QuizBlue
import com.example.quizatron.ui.theme.QuizYellowDeep
import kotlinx.coroutines.delay

/** VIEW (resultado): parabeniza o jogador pelo nome, mostra estrelas e os acertos. */
@Composable
fun ResultScreen(
    state: QuizUiState,
    onPlayAgain: () -> Unit,
) {
    BackHandler(onBack = onPlayAgain)

    val perfect = state.total > 0 && state.score == state.total
    val name = state.playerName.trim()
    val greeting = when {
        perfect -> "Parabéns"
        state.score > 0 -> "Bom trabalho"
        else -> "Não desista"
    }
    val emoji = when {
        perfect -> "🏆"
        state.score > 0 -> "👏"
        else -> "💪"
    }
    val message = if (name.isNotEmpty()) "$greeting, $name!" else "$greeting!"

    // O número sobe de 0 até o placar final
    var target by remember { mutableIntStateOf(0) }
    LaunchedEffect(state.score) { target = state.score }
    val shownScore by animateIntAsState(
        targetValue = target,
        animationSpec = tween(durationMillis = 900, delayMillis = 300),
        label = "placar",
    )

    BubbleBackground(base = QuizBlue) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            QuizLogo(size = 64.dp)
            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 10.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = emoji, fontSize = 56.sp)
                    Spacer(Modifier.height(10.dp))
                    PillLabel(message)
                    Spacer(Modifier.height(22.dp))

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$shownScore",
                            color = Ink,
                            fontSize = 72.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                        Text(
                            text = " / ${state.total}",
                            color = InkSoft,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(state.total) { i -> ResultStar(index = i, filled = i < state.score) }
                    }
                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "Você acertou ${state.score} de ${state.total} perguntas",
                        color = Ink,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                text = "JOGAR NOVAMENTE",
                onClick = onPlayAgain,
                icon = Icons.Filled.Refresh,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }

        // Confete por cima de tudo, só quando acerta todas
        if (perfect) Confetti()
    }
}

/** Estrela que "pula" para dentro da tela, uma depois da outra. */
@Composable
private fun ResultStar(index: Int, filled: Boolean) {
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(400L + index * 220L)
        scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
    Icon(
        imageVector = Icons.Filled.Star,
        contentDescription = if (filled) "Estrela conquistada" else "Estrela não conquistada",
        tint = if (filled) QuizYellowDeep else Color(0xFFDADAE2),
        modifier = Modifier
            .size(44.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            },
    )
}
