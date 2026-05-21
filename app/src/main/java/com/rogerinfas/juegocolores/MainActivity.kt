/**
 * App: JuegoColores
 * Autor: Roger
 * Descripcion: Actividad principal que inicializa la navegacion entre pantallas del juego.
 */
package com.rogerinfas.juegocolores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rogerinfas.juegocolores.ui.screens.GameScreen
import com.rogerinfas.juegocolores.ui.screens.ResultScreen
import com.rogerinfas.juegocolores.ui.screens.WelcomeScreen
import com.rogerinfas.juegocolores.ui.theme.JuegoDeColoresTheme
import com.rogerinfas.juegocolores.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JuegoDeColoresTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // Definimos el ViewModel aquí para que sea compartido por todas las pantallas
    val viewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onStartGame = { navController.navigate("game") }
            )
        }

        composable("game") {
            GameScreen(
                onGameFinished = {
                    navController.navigate("result") {
                        popUpTo("game") { inclusive = true }
                    }
                }
            )
        }

        composable("result") {
            ResultScreen(
                onPlayAgain = {
                    navController.navigate("game") {
                        popUpTo("result") { inclusive = true }
                    }
                }
            )
        }
    }
}
