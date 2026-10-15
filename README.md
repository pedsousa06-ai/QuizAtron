# Quizatron 3000 (Kotlin + Jetpack Compose + MVVM)

## Abrir e rodar

1. Android Studio > **File > Open** e escolha a pasta `QuizatronApp` (a que contém `settings.gradle.kts`).
2. Espere o **Gradle Sync** terminar. Na primeira vez ele baixa o Gradle 8.9 e as bibliotecas,
   então precisa de internet. Se pedir para instalar o **Android SDK 35**, aceite.
3. Escolha um emulador (ou celular com depuração USB) e clique em **Run**.

Requisitos: Android Studio Ladybug (2024.2) ou mais novo, JDK 17 (o que vem no Android Studio serve).
Android mínimo do app: 8.0 (API 26).

Se o Android Studio sugerir atualizar o Android Gradle Plugin, pode aceitar.

## Estrutura MVVM

| Camada | Arquivos |
|---|---|
| Model | `data/model/Question.kt`, `data/repository/QuizRepository.kt` |
| ViewModel | `ui/quiz/QuizViewModel.kt`, `ui/quiz/QuizUiState.kt` |
| View | `ui/home`, `ui/quiz/QuizScreen.kt`, `ui/result`, `ui/components`, `ui/navigation` |

Desafio 2: nome do jogador na tela inicial e na mensagem do resultado.
Desafio 3: resposta certa em verde; errada em vermelho com a certa em verde, antes de trocar de tela.
