package com.example.quizatron.ui.quiz

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizatron.data.model.Question
import com.example.quizatron.ui.components.BubbleBackground
import com.example.quizatron.ui.components.PillLabel
import com.example.quizatron.ui.components.QuizLogo
import com.example.quizatron.ui.theme.AnswerCorrect
import com.example.quizatron.ui.theme.AnswerCorrectStrong
import com.example.quizatron.ui.theme.AnswerIdleBorder
import com.example.quizatron.ui.theme.AnswerWrong
import com.example.quizatron.ui.theme.AnswerWrongStrong
import com.example.quizatron.ui.theme.BadgeIdle
import com.example.quizatron.ui.theme.Ink
import com.example.quizatron.ui.theme.QuizGreen
import com.example.quizatron.ui.theme.QuizPink
import com.example.quizatron.ui.theme.QuizYellowDeep

private enum class OptionStatus { Idle, Correct, Wrong }

/** Foto da pergunta exibida. A troca de pergunta anima; a troca de seleção não. */
private data class QuizFrame(val index: Int, val question: Question, val selectedIndex: Int?)

/**
 * VIEW (tela do quiz). Após o toque, a resposta certa fica verde; se o jogador errou,
 * a escolhida fica vermelha (e treme) e a certa continua verde. A troca de pergunta é do ViewModel.
 */
@Composable
fun QuizScreen(
    state: QuizUiState,
    onAnswer: (Int) -> Unit,
    onExit: () -> Unit,
) {
    // Voltar durante o jogo cancela o feedback pendente e volta ao início
    BackHandler(onBack = onExit)

    val question = state.currentQuestion ?: return
    val haptic = LocalHapticFeedback.current
    val frame = QuizFrame(state.currentIndex, question, state.selectedIndex)

    // A barra enche assim que a pergunta é respondida
    val answered = state.currentIndex + if (state.selectedIndex != null) 1 else 0
    val progress by animateFloatAsState(
        targetValue = answered.toFloat() / state.total.coerceAtLeast(1),
        animationSpec = tween(500),
        label = "progresso",
    )

    BubbleBackground(base = QuizPink) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onExit) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar ao início",
                        tint = Ink,
                    )
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    QuizLogo(size = 52.dp)
                }
                ScoreChip(score = state.score)
            }

            Spacer(Modifier.height(12.dp))
            PillLabel("Pergunta ${state.currentIndex + 1} de ${state.total}")
            Spacer(Modifier.height(16.dp))
            ProgressBar(progress = progress)
            Spacer(Modifier.height(20.dp))

            AnimatedContent(
                targetState = frame,
                contentKey = { it.index },
                transitionSpec = {
                    (slideInHorizontally(tween(350)) { it / 2 } + fadeIn(tween(350))) togetherWith
                        (slideOutHorizontally(tween(250)) { -it / 2 } + fadeOut(tween(200)))
                },
                label = "pergunta",
            ) { f ->
                QuestionCard(
                    question = f.question,
                    selectedIndex = f.selectedIndex,
                    onAnswer = { index ->
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onAnswer(index)
                    },
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuestionCard(question: Question, selectedIndex: Int?, onAnswer: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = question.text,
                color = Ink,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            question.options.forEachIndexed { index, option ->
                val status = when {
                    selectedIndex == null -> OptionStatus.Idle
                    index == question.correctIndex -> OptionStatus.Correct
                    index == selectedIndex -> OptionStatus.Wrong
                    else -> OptionStatus.Idle
                }
                OptionItem(
                    letter = 'A' + index,
                    text = option,
                    status = status,
                    enabled = selectedIndex == null,
                    onClick = { onAnswer(index) },
                )
            }
        }
    }
}

@Composable
private fun OptionItem(
    letter: Char,
    text: String,
    status: OptionStatus,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val container by animateColorAsState(
        targetValue = when (status) {
            OptionStatus.Idle -> Color.White
            OptionStatus.Correct -> AnswerCorrect
            OptionStatus.Wrong -> AnswerWrong
        },
        animationSpec = tween(250),
        label = "cor",
    )
    val scale by animateFloatAsState(
        targetValue = if (status == OptionStatus.Correct) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "escala",
    )
    // A resposta errada dá uma tremidinha
    val shake = remember { Animatable(0f) }
    LaunchedEffect(status) {
        if (status == OptionStatus.Wrong) {
            repeat(3) {
                shake.animateTo(14f, tween(45))
                shake.animateTo(-14f, tween(45))
            }
            shake.animateTo(0f, tween(45))
        }
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = shake.value
            },
        shape = RoundedCornerShape(16.dp),
        color = container,
        border = BorderStroke(
            width = if (status == OptionStatus.Idle) 1.5.dp else 2.dp,
            color = if (status == OptionStatus.Idle) AnswerIdleBorder else Ink,
        ),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val badgeColor = when (status) {
                OptionStatus.Idle -> BadgeIdle
                OptionStatus.Correct -> AnswerCorrectStrong
                OptionStatus.Wrong -> AnswerWrongStrong
            }
            Box(
                modifier = Modifier.size(34.dp).clip(CircleShape).background(badgeColor),
                contentAlignment = Alignment.Center,
            ) {
                // Além da cor, um símbolo: quem não distingue verde/vermelho também entende
                when (status) {
                    OptionStatus.Correct -> Icon(Icons.Filled.Check, "Resposta correta", tint = Color.White, modifier = Modifier.size(20.dp))
                    OptionStatus.Wrong -> Icon(Icons.Filled.Close, "Resposta errada", tint = Color.White, modifier = Modifier.size(20.dp))
                    OptionStatus.Idle -> Text(letter.toString(), color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
            Text(
                text = text,
                color = Ink,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ScoreChip(score: Int) {
    // Dá um "pulinho" sempre que o placar muda
    val bump = remember { Animatable(1f) }
    LaunchedEffect(score) {
        bump.snapTo(1.3f)
        bump.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
    Surface(
        modifier = Modifier.graphicsLayer {
            scaleX = bump.value
            scaleY = bump.value
        },
        shape = RoundedCornerShape(50),
        color = Color.White,
        border = BorderStroke(1.5.dp, Ink),
        shadowElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Star, contentDescription = "Acertos", tint = QuizYellowDeep, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(4.dp))
            Text(text = "$score", color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun ProgressBar(progress: Float) {
    val shape = RoundedCornerShape(50)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(14.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.55f))
            .border(1.5.dp, Ink, shape),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(QuizGreen),
        )
    }
}
