package com.inacap.iotmobileapp.ui.barrier

import android.app.Application
import android.util.Log
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

    init {
        // Iniciar polling automáticamente cuando se crea el ViewModel
        startPollingBarrierStatus()
    }

    /**
     * POLLING: Consulta el estado de la barrera cada 2 segundos
     * Mantiene sincronizada la app con el backend en tiempo real
     */
    private fun startPollingBarrierStatus() {
        viewModelScope.launch {
            while (true) {
                try {
                    val response = RetrofitClient.rfidSensorApiService.getBarrierStatus()

                    if (response.isSuccessful) {
                        val status = response.body()

                        // Actualizar estado según respuesta del backend
                        _barrierState.value = when (status?.estado) {
                            "ABIERTA" -> BarrierState.OPEN
                            "CERRADA" -> BarrierState.CLOSED
                            else -> BarrierState.UNKNOWN
                        }

                        // Actualizar mensaje con información del tiempo abierta
                        if (status?.estado == "ABIERTA" && status.tiempoAbierta != null) {
                            val tiempoRestante = 10 - status.tiempoAbierta
                            _message.value = "Barrera abierta (cierre automático en ${tiempoRestante}s)"
                        } else if (status?.estado == "CERRADA") {
                            if (status.ultimoEvento == "AUTO_CIERRE") {
                                _message.value = "Barrera cerrada automáticamente"
                            } else if (status.ultimoEvento == "CIERRE_MANUAL") {
                                _message.value = "Barrera cerrada manualmente"
                            }
                        }

                        Log.d("BarrierPolling", "Estado actualizado: ${status?.estado}")
                    } else {
                        Log.e("BarrierPolling", "Error: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("BarrierPolling", "Error consultando estado: ${e.message}")
                    // No actualizar estado si hay error de conexión
                }

                // Esperar 2 segundos antes de volver a consultar
                delay(2000)
            }
        }
    }

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
                    _isLoading.value = false
                    return@launch
                }

                val token = user.token ?: ""
                val authToken = if (token.startsWith("Bearer")) token else "Bearer $token"

                val response = RetrofitClient.rfidSensorApiService.openBarrier(authToken)

                if (response.isSuccessful) {
                    _message.value = response.body()?.mensaje ?: "Comando de apertura enviado"
                    // El estado se actualizará automáticamente por el polling (cada 2 seg)
                } else {
                    _message.value = "Error: ${response.message()}"
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
                    _isLoading.value = false
                    return@launch
                }

                val token = user.token ?: ""
                val authToken = if (token.startsWith("Bearer")) token else "Bearer $token"

                val response = RetrofitClient.rfidSensorApiService.closeBarrier(authToken)

                if (response.isSuccessful) {
                    _message.value = response.body()?.mensaje ?: "Comando de cierre enviado"
                    // El estado se actualizará automáticamente por el polling (cada 2 seg)
                } else {
                    _message.value = "Error: ${response.message()}"
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
