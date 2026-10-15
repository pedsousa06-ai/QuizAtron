package com.example.quizatron.ui.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizatron.ui.home.HomeScreen
import com.example.quizatron.ui.quiz.QuizEvent
import com.example.quizatron.ui.quiz.QuizScreen
import com.example.quizatron.ui.quiz.QuizViewModel
import com.example.quizatron.ui.result.ResultScreen

private object Routes {
    const val HOME = "home"
    const val QUIZ = "quiz"
    const val RESULT = "result"
}

/**
 * Liga as três telas a UM ViewModel (escopo da Activity). As telas não navegam sozinhas:
 * o ViewModel manda um evento e este código executa a navegação.
 */
@Composable
fun QuizApp(viewModel: QuizViewModel = viewModel()) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                QuizEvent.NavigateToQuiz -> navController.navigate(Routes.QUIZ)
                QuizEvent.NavigateToResult -> navController.navigate(Routes.RESULT) {
                    // Tira o quiz da pilha: voltar do resultado não cai numa pergunta já respondida
                    popUpTo(Routes.QUIZ) { inclusive = true }
                }
                QuizEvent.NavigateToHome -> navController.navigate(Routes.HOME) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        enterTransition = { slideInHorizontally(tween(350)) { it / 3 } + fadeIn(tween(350)) },
        exitTransition = { fadeOut(tween(250)) },
        popEnterTransition = { fadeIn(tween(350)) },
        popExitTransition = { slideOutHorizontally(tween(350)) { it / 3 } + fadeOut(tween(250)) },
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                state = state,
                onNameChange = viewModel::onNameChange,
                onStart = viewModel::startQuiz,
            )
        }
        composable(Routes.QUIZ) {
            QuizScreen(
                state = state,
                onAnswer = viewModel::onAnswerSelected,
                onExit = viewModel::backToHome,
            )
        }
        composable(Routes.RESULT) {
            ResultScreen(state = state, onPlayAgain = viewModel::backToHome)
        }
    }
}
