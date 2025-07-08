package com.example.lab6

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PantallaInicio(navController: NavController) {
    var dimensionInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = dimensionInput,
            onValueChange = { dimensionInput = it },
            label = { Text("Tamaño de la matriz de Bingo") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            dimensionInput.toIntOrNull()?.takeIf { it > 0 }?.let { size ->
                val userId = crearCodigoJugador()
                navController.navigate("bingo/$size/$userId")
            }
        }) {
            Text("CREAR CARTA")
        }
    }
}

fun crearCodigoJugador(): String {
    val letras = ('A'..'Z') + ('a'..'z')
    val parteAlfa = (1..3).map { letras.random() }.joinToString("")
    val parteNum = (100..999).random()
    return "$parteAlfa-$parteNum"
}
