package com.example.quizatron.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizatron.ui.components.BubbleBackground
import com.example.quizatron.ui.components.PrimaryButton
import com.example.quizatron.ui.components.QuizLogo
import com.example.quizatron.ui.quiz.QuizUiState
import com.example.quizatron.ui.theme.Ink
import com.example.quizatron.ui.theme.InkSoft
import com.example.quizatron.ui.theme.QuizBlue
import com.example.quizatron.ui.theme.QuizPink

/** VIEW (tela inicial): pede o nome do jogador e só libera o botão com o nome preenchido. */
@Composable
fun HomeScreen(
    state: QuizUiState,
    onNameChange: (String) -> Unit,
    onStart: () -> Unit,
) {
    BubbleBackground(base = QuizBlue) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        ) {
            QuizLogo(size = 170.dp, animated = true)
            Spacer(Modifier.height(28.dp))

            Text(
                text = "QUIZATRON",
                color = Ink,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
            )
            Spacer(Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(50),
                color = QuizPink,
                border = BorderStroke(1.5.dp, Ink),
            ) {
                Text(
                    text = "3000",
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp),
                    color = Ink,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                text = "Responda ${state.total} perguntas e descubra o quanto você sabe!",
                color = InkSoft,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(28.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        text = "Como podemos te chamar?",
                        color = Ink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = state.playerName,
                        onValueChange = onNameChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Seu nome") },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(onDone = { if (state.canStart) onStart() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Ink,
                            unfocusedBorderColor = Color(0xFFD0D0DA),
                            cursorColor = Ink,
                            focusedLeadingIconColor = Ink,
                        ),
                    )
                    Spacer(Modifier.height(18.dp))
                    PrimaryButton(
                        text = "COMEÇAR!",
                        onClick = onStart,
                        enabled = state.canStart,
                        icon = Icons.Filled.PlayArrow,
                    )
                }
            }
        }
    }
}
