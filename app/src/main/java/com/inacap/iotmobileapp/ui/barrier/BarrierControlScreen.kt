package com.inacap.iotmobileapp.ui.barrier

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarrierControlScreen(
    viewModel: BarrierControlViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val barrierState by viewModel.barrierState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Barrera") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            // Título
            Text(
                text = "Llavero Digital",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Estado visual de la barrera
            BarrierStatusIndicator(state = barrierState, isLoading = isLoading)

            // Botones de control
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botón Abrir
                Button(
                    onClick = { viewModel.openBarrier() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    enabled = !isLoading && barrierState != BarrierState.OPENING
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "ABRIR BARRERA",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Botón Cerrar
                OutlinedButton(
                    onClick = { viewModel.closeBarrier() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    border = BorderStroke(2.dp, Color(0xFFF44336)),
                    enabled = !isLoading && barrierState != BarrierState.CLOSING
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color(0xFFF44336)
                        )
                        Text(
                            text = "CERRAR BARRERA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFF44336)
                        )
                    }
                }
            }

            // Mensaje de estado
            message?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null)
                        Text(
                            "Información",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        "• La barrera se cerrará automáticamente después de 10 segundos",
                        fontSize = 14.sp
                    )
                    Text(
                        "• Todos los accesos quedan registrados en el historial",
                        fontSize = 14.sp
                    )
                    Text(
                        "• Solo usuarios activos pueden controlar la barrera",
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BarrierStatusIndicator(state: BarrierState, isLoading: Boolean) {
    // Animación de escala pulsante
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Color animado según estado
    val color by animateColorAsState(
        targetValue = when (state) {
            BarrierState.OPEN -> Color(0xFF4CAF50)
            BarrierState.CLOSED -> Color(0xFFF44336)
            BarrierState.OPENING -> Color(0xFFFF9800)
            BarrierState.CLOSING -> Color(0xFFFF9800)
            BarrierState.UNKNOWN -> Color.Gray
        },
        label = "color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Indicador circular
        Surface(
            modifier = Modifier
                .size(200.dp)
                .scale(if (isLoading) scale else 1f),
            shape = CircleShape,
            color = color.copy(alpha = 0.2f),
            border = BorderStroke(4.dp, color)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = color,
                        strokeWidth = 6.dp
                    )
                } else {
                    Icon(
                        imageVector = when (state) {
                            BarrierState.OPEN -> Icons.Default.LockOpen
                            BarrierState.CLOSED -> Icons.Default.Lock
                            BarrierState.OPENING -> Icons.Default.KeyboardArrowUp
                            BarrierState.CLOSING -> Icons.Default.KeyboardArrowDown
                            BarrierState.UNKNOWN -> Icons.Default.Help
                        },
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = color
                    )
                }
            }
        }

        // Texto de estado
        Text(
            text = when (state) {
                BarrierState.OPEN -> "BARRERA ABIERTA"
                BarrierState.CLOSED -> "BARRERA CERRADA"
                BarrierState.OPENING -> "ABRIENDO..."
                BarrierState.CLOSING -> "CERRANDO..."
                BarrierState.UNKNOWN -> "ESTADO DESCONOCIDO"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
