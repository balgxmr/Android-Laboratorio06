package com.example.lab6

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun PantallaBingo(tamano: Int, codigo: String) {
    val contexto = LocalContext.current
    val tts = remember {
        TextToSpeech(contexto) { estado ->
            if (estado == TextToSpeech.SUCCESS) {
                // Nada más se inicializa el idioma después
            }
        }.apply {
            language = Locale("es", "ES")
        }
    }

    var numeros by remember { mutableStateOf(generarNumerosUnicos(tamano)) }
    var seleccion by remember { mutableStateOf(MutableList(tamano * tamano) { false }) }
    var mostrarBingo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Jugador: $codigo", fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(tamano),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(numeros) { idx, num ->
                val activo = seleccion[idx]
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (activo) Color(0xFF0D47A1) else Color(0xFF4CAF50),
                            shape = MaterialTheme.shapes.medium
                        )
                        .clickable(enabled = !activo) {
                            seleccion = seleccion.toMutableList().also { it[idx] = true }
                            if (hayBingo(seleccion, tamano)) {
                                mostrarBingo = true
                                tts.speak("¡Has hecho Bingo!", TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (activo) "✔" else num.toString(), color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            numeros = generarNumerosUnicos(tamano)
            seleccion = MutableList(tamano * tamano) { false }
        }) {
            Text("NUEVA CARTA")
        }
    }

    if (mostrarBingo) {
        AlertDialog(
            onDismissRequest = { mostrarBingo = false },
            confirmButton = {
                TextButton(onClick = { mostrarBingo = false }) {
                    Text("Cerrar")
                }
            },
            title = { Text("¡BINGO!") },
            text = { Text("¡Felicidades! Has ganado la partida.") }
        )
    }
}

fun generarNumerosUnicos(size: Int): List<Int> {
    return (1..90).shuffled().take(size * size)
}

fun hayBingo(seleccionados: List<Boolean>, size: Int): Boolean {
    return lineaHorizontal(seleccionados, size) ||
            lineaVertical(seleccionados, size) ||
            diagonalPrincipal(seleccionados, size) ||
            diagonalSecundaria(seleccionados, size)
}

fun lineaHorizontal(marcado: List<Boolean>, dim: Int): Boolean =
    (0 until dim).any { fila -> (0 until dim).all { col -> marcado[fila * dim + col] } }

fun lineaVertical(marcado: List<Boolean>, dim: Int): Boolean =
    (0 until dim).any { col -> (0 until dim).all { fila -> marcado[fila * dim + col] } }

fun diagonalPrincipal(marcado: List<Boolean>, dim: Int): Boolean =
    (0 until dim).all { i -> marcado[i * dim + i] }

fun diagonalSecundaria(marcado: List<Boolean>, dim: Int): Boolean =
    (0 until dim).all { i -> marcado[i * dim + (dim - i - 1)] }