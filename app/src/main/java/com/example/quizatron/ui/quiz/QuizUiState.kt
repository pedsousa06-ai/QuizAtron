package com.example.quizatron.ui.quiz

import com.example.quizatron.data.model.Question

/** Tudo o que as telas precisam desenhar. As telas só LEEM este estado. */
data class QuizUiState(
    val playerName: String = "",
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    /** Opção tocada na pergunta atual. Enquanto não for null, a tela mostra o feedback de cor. */
    val selectedIndex: Int? = null,
    val score: Int = 0,
) {
    val total: Int get() = questions.size
    val currentQuestion: Question? get() = questions.getOrNull(currentIndex)
    val canStart: Boolean get() = playerName.isNotBlank()
}

/** Eventos de navegação enviados pelo ViewModel (consumidos uma única vez). */
sealed interface QuizEvent {
    data object NavigateToQuiz : QuizEvent
    data object NavigateToResult : QuizEvent
    data object NavigateToHome : QuizEvent
}
