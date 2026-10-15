package com.example.quizatron.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizatron.data.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL: guarda o estado do jogo e as regras (pontuação, avanço de pergunta).
 * Não conhece Compose nem Context, então sobrevive à rotação de tela.
 * Uma única instância é compartilhada pelas três telas.
 */
class QuizViewModel(
    private val repository: QuizRepository = QuizRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState(questions = repository.getQuestions()))
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private val _events = Channel<QuizEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var feedbackJob: Job? = null

    fun onNameChange(name: String) {
        _uiState.update { it.copy(playerName = name.take(MAX_NAME_LENGTH)) }
    }

    fun startQuiz() {
        if (!_uiState.value.canStart) return
        feedbackJob?.cancel()
        _uiState.update {
            it.copy(
                questions = repository.getQuestions(),
                currentIndex = 0,
                selectedIndex = null,
                score = 0,
            )
        }
        _events.trySend(QuizEvent.NavigateToQuiz)
    }

    fun onAnswerSelected(index: Int) {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        // Já respondeu: ignora toques repetidos durante o feedback de cor
        if (state.selectedIndex != null) return

        _uiState.update {
            it.copy(
                selectedIndex = index,
                score = if (index == question.correctIndex) it.score + 1 else it.score,
            )
        }

        // Mostra verde/vermelho por um instante e só depois troca de pergunta
        feedbackJob = viewModelScope.launch {
            delay(FEEDBACK_DELAY_MS)
            goToNext()
        }
    }

    private fun goToNext() {
        val state = _uiState.value
        if (state.currentIndex + 1 < state.total) {
            _uiState.update { it.copy(currentIndex = it.currentIndex + 1, selectedIndex = null) }
        } else {
            _events.trySend(QuizEvent.NavigateToResult)
        }
    }

    /**
     * Volta ao início. Não zera o placar aqui: a tela atual continua visível durante a animação
     * de navegação e mostraria "0 acertos" por um instante. O reset acontece em [startQuiz].
     */
    fun backToHome() {
        feedbackJob?.cancel()
        _events.trySend(QuizEvent.NavigateToHome)
    }

    private companion object {
        const val FEEDBACK_DELAY_MS = 1200L
        const val MAX_NAME_LENGTH = 20
    }
}
