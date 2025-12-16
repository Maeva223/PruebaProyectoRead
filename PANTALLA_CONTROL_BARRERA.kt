/**
 * Pantalla de Control de Barrera
 * Permite abrir y cerrar la barrera manualmente desde la app
 * Funcionalidad "Llavero Digital"
 *
 * Ubicación final: app/src/main/java/com/inacap/iotmobileapp/ui/barrier/BarrierControlScreen.kt
 */

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
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Color animado según estado
    val color by animateColorAsState(
        targetValue = when (state) {
            BarrierState.OPEN -> Color(0xFF4CAF50)
            BarrierState.CLOSED -> Color(0xFFF44336)
            BarrierState.OPENING -> Color(0xFFFF9800)
            BarrierState.CLOSING -> Color(0xFFFF9800)
            BarrierState.UNKNOWN -> Color.Gray
        }
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

// ViewModel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.api.RetrofitClient
import com.inacap.iotmobileapp.utils.UserSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class BarrierState {
    OPEN,
    CLOSED,
    OPENING,
    CLOSING,
    UNKNOWN
}

class BarrierControlViewModel(application: Application) : AndroidViewModel(application) {

    private val _barrierState = MutableStateFlow(BarrierState.CLOSED)
    val barrierState: StateFlow<BarrierState> = _barrierState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun openBarrier() {
        viewModelScope.launch {
            _isLoading.value = true
            _barrierState.value = BarrierState.OPENING
            _message.value = "Enviando comando de apertura..."

            try {
                val user = UserSession.currentUser
                if (user == null) {
                    _message.value = "Error: Usuario no autenticado"
                    _barrierState.value = BarrierState.UNKNOWN
                    return@launch
                }

                val token = "Bearer ${user.token}"
                val response = RetrofitClient.sensorApi.openBarrier(token)

                if (response.isSuccessful) {
                    _barrierState.value = BarrierState.OPEN
                    _message.value = response.body()?.mensaje ?: "Barrera abierta"

                    // Simular cierre automático después de 10 segundos
                    delay(10000)
                    _barrierState.value = BarrierState.CLOSED
                    _message.value = "Barrera cerrada automáticamente"
                } else {
                    _message.value = "Error: ${response.message()}"
                    _barrierState.value = BarrierState.UNKNOWN
                }
            } catch (e: Exception) {
                _message.value = "Error de conexión: ${e.message}"
                _barrierState.value = BarrierState.UNKNOWN
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun closeBarrier() {
        viewModelScope.launch {
            _isLoading.value = true
            _barrierState.value = BarrierState.CLOSING
            _message.value = "Enviando comando de cierre..."

            try {
                val user = UserSession.currentUser
                if (user == null) {
                    _message.value = "Error: Usuario no autenticado"
                    _barrierState.value = BarrierState.UNKNOWN
                    return@launch
                }

                val token = "Bearer ${user.token}"
                val response = RetrofitClient.sensorApi.closeBarrier(token)

                if (response.isSuccessful) {
                    _barrierState.value = BarrierState.CLOSED
                    _message.value = response.body()?.mensaje ?: "Barrera cerrada"
                } else {
                    _message.value = "Error: ${response.message()}"
                    _barrierState.value = BarrierState.UNKNOWN
                }
            } catch (e: Exception) {
                _message.value = "Error de conexión: ${e.message}"
                _barrierState.value = BarrierState.UNKNOWN
            } finally {
                _isLoading.value = false
            }
        }
    }
}
