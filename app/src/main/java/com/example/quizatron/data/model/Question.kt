package com.example.quizatron.data.model

/** MODEL: uma pergunta do quiz. [correctIndex] é a posição da resposta certa em [options]. */
data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int,
) {
    init {
        require(correctIndex in options.indices) { "correctIndex fora das opções" }
    }
}
