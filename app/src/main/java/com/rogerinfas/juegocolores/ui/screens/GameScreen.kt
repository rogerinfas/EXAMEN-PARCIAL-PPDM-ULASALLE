/**
 * App: JuegoColores
 * Autor: Roger
 * Descripcion: Pantalla principal del juego con color aleatorio, botones de respuesta, temporizador y puntaje.
 */
package com.rogerinfas.juegocolores.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rogerinfas.juegocolores.R
import com.rogerinfas.juegocolores.viewmodel.GameColor
import com.rogerinfas.juegocolores.viewmodel.GameViewModel

@Composable
fun GameScreen(onGameFinished: () -> Unit) {
    val viewModel: GameViewModel = viewModel()

    val currentColor by viewModel.currentColor.collectAsState()
    val score by viewModel.score.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val gameFinished by viewModel.gameFinished.collectAsState()
    val feedback by viewModel.feedback.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startGame()
    }

    LaunchedEffect(gameFinished) {
        if (gameFinished) onGameFinished()
    }

    val animatedColor by animateColorAsState(
        targetValue = mapGameColor(currentColor),
        animationSpec = tween(durationMillis = 400),
        label = "colorAnimation"
    )

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Encabezado con puntaje y tiempo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.label_score),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$score",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.label_time),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$timeRemaining s",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timeRemaining <= 10) Color.Red else MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Circulo con el color aleatorio
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(animatedColor)
            )

            // Feedback al usuario
            Text(
                text = feedback,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = if (feedback.startsWith("¡")) Color(0xFF2E7D32) else Color(0xFFC62828)
            )

            // Botones de colores
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ColorButton(
                        gameColor = GameColor.ROJO,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.checkAnswer(GameColor.ROJO) }
                    )
                    ColorButton(
                        gameColor = GameColor.VERDE,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.checkAnswer(GameColor.VERDE) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ColorButton(
                        gameColor = GameColor.AZUL,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.checkAnswer(GameColor.AZUL) }
                    )
                    ColorButton(
                        gameColor = GameColor.AMARILLO,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.checkAnswer(GameColor.AMARILLO) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ColorButton(
                        gameColor = GameColor.MORADO,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        onClick = { viewModel.checkAnswer(GameColor.MORADO) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ColorButton(
    gameColor: GameColor,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = mapGameColor(gameColor))
    ) {
        Text(
            text = gameColor.label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

private fun mapGameColor(gameColor: GameColor): Color {
    return when (gameColor) {
        GameColor.ROJO -> Color(0xFFE53935)
        GameColor.VERDE -> Color(0xFF43A047)
        GameColor.AZUL -> Color(0xFF1E88E5)
        GameColor.AMARILLO -> Color(0xFFFDD835)
        GameColor.MORADO -> Color(0xFF8E24AA)
    }
}
