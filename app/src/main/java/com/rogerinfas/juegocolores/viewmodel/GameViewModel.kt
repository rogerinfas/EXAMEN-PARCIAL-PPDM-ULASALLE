/**
 * App: JuegoColores
 * Autor: Roger
 * Descripcion: ViewModel principal que maneja la logica del juego, temporizador, puntaje y estado de la partida.
 */
package com.rogerinfas.juegocolores.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Colores disponibles en el juego
enum class GameColor(val label: String) {
    ROJO("Rojo"),
    VERDE("Verde"),
    AZUL("Azul"),
    AMARILLO("Amarillo"),
    MORADO("Morado")
}

data class GameSession(
    val sessionNumber: Int,
    val score: Int
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val PREFS_NAME = "juego_colores_prefs"
    private val KEY_HIGH_SCORE = "high_score"
    private val TIMER_SECONDS = 30

    private val _currentColor = MutableStateFlow(GameColor.ROJO)
    val currentColor: StateFlow<GameColor> = _currentColor

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _timeRemaining = MutableStateFlow(TIMER_SECONDS)
    val timeRemaining: StateFlow<Int> = _timeRemaining

    private val _gameFinished = MutableStateFlow(false)
    val gameFinished: StateFlow<Boolean> = _gameFinished

    private val _feedback = MutableStateFlow("")
    val feedback: StateFlow<String> = _feedback

    private val _highScore = MutableStateFlow(0)
    val highScore: StateFlow<Int> = _highScore

    private val _sessionHistory = MutableStateFlow<List<GameSession>>(emptyList())
    val sessionHistory: StateFlow<List<GameSession>> = _sessionHistory

    private var timerJob: Job? = null
    private var sessionCount = 0

    init {
        loadHighScore()
    }

    fun startGame() {
        _score.value = 0
        _timeRemaining.value = TIMER_SECONDS
        _gameFinished.value = false
        _feedback.value = ""
        generateRandomColor()
        startTimer()
    }

    fun checkAnswer(selectedColor: GameColor) {
        if (_gameFinished.value) return

        if (selectedColor == _currentColor.value) {
            _score.value++
            _feedback.value = "¡Correcto!"
            generateRandomColor()
        } else {
            _feedback.value = "Incorrecto, era ${_currentColor.value.label}"
        }
    }

    private fun generateRandomColor() {
        val colors = GameColor.entries.toTypedArray()
        _currentColor.value = colors.random()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0) {
                delay(1000L)
                _timeRemaining.value--
            }
            finishGame()
        }
    }

    private fun finishGame() {
        _gameFinished.value = true
        timerJob?.cancel()
        saveSessionToHistory()
        updateHighScore()
    }

    private fun saveSessionToHistory() {
        sessionCount++
        val newSession = GameSession(
            sessionNumber = sessionCount,
            score = _score.value
        )
        _sessionHistory.value = _sessionHistory.value + newSession
    }

    private fun updateHighScore() {
        if (_score.value > _highScore.value) {
            _highScore.value = _score.value
            saveHighScore(_score.value)
        }
    }

    private fun saveHighScore(newScore: Int) {
        val prefs = getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_HIGH_SCORE, newScore).apply()
    }

    private fun loadHighScore() {
        val prefs = getApplication<Application>().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _highScore.value = prefs.getInt(KEY_HIGH_SCORE, 0)
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
