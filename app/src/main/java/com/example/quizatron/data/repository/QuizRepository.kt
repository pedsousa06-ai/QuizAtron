package com.example.quizatron.data.repository

import com.example.quizatron.data.model.Question

/**
 * MODEL: fonte das perguntas. Hoje é uma lista local; se um dia vierem de uma API,
 * só esta classe muda e o ViewModel continua igual.
 */
class QuizRepository {
    fun getQuestions(): List<Question> = listOf(
        Question(
            text = "Qual é a capital da França?",
            options = listOf("Londres", "Berlim", "Paris", "Madri"),
            correctIndex = 2,
        ),
        Question(
            text = "Qual planeta é conhecido como planeta vermelho?",
            options = listOf("Vênus", "Marte", "Júpiter", "Saturno"),
            correctIndex = 1,
        ),
        Question(
            text = "Qual é o maior oceano do mundo?",
            options = listOf("Atlântico", "Índico", "Pacífico", "Ártico"),
            correctIndex = 2,
        ),
    )
}
