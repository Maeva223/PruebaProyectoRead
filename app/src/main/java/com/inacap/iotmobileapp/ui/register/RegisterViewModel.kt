package com.inacap.iotmobileapp.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.api.RegisterRequest
import com.inacap.iotmobileapp.data.api.RetrofitClient
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.database.entities.User
import com.inacap.iotmobileapp.data.repository.UserRepository
import com.inacap.iotmobileapp.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao(), database.recoveryCodeDao())
    
    // Usamos el servicio del Backend para el registro
    private val backendService = RetrofitClient.backendApiService
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onNombresChange(value: String) { _uiState.value = _uiState.value.copy(nombres = value, errorMessage = "") }
    fun onApellidosChange(value: String) { _uiState.value = _uiState.value.copy(apellidos = value, errorMessage = "") }
    fun onEmailChange(value: String) { _uiState.value = _uiState.value.copy(email = value, errorMessage = "") }
    fun onPasswordChange(value: String) { _uiState.value = _uiState.value.copy(password = value, errorMessage = "") }
    fun onConfirmPasswordChange(value: String) { _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = "") }

    fun onRegister(onSuccess: () -> Unit) {
        val nombres = _uiState.value.nombres.trim()
        val apellidos = _uiState.value.apellidos.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        // Validaciones básicas
        if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Campos obligatorios vacíos")
            return
        }
        if (!Validators.isValidName(nombres) || !Validators.isValidName(apellidos)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Nombres y apellidos solo letras")
            return
        }
        if (!Validators.isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email inválido")
            return
        }
        if (!Validators.isValidPassword(password)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Contraseña débil. ${Validators.getPasswordErrorMessage(password)}")
            return
        }
        if (password != _uiState.value.confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Contraseñas no coinciden")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            try {
                // 1. REGISTRO EN BACKEND (AWS)
                val request = RegisterRequest(
                    name = nombres,
                    lastName = apellidos,
                    email = email,
                    password = password
                )
                
                val response = backendService.registerUser(request)
                
                if (response.isSuccessful) {
                    // 2. REGISTRO LOCAL (Solo si el backend dijo OK)
                    val userLocal = User(nombres = nombres, apellidos = apellidos, email = email, password = password)
                    val localResult = repository.registerUser(userLocal)
                    
                    localResult.fold(
                        onSuccess = {
                            _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Registro exitoso (Nube + Local)")
                            kotlinx.coroutines.delay(1500)
                            onSuccess()
                        },
                        onFailure = {
                            // Si falló local pero guardó en nube, avisamos
                             _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Registro exitoso (Solo Nube)")
                            kotlinx.coroutines.delay(1500)
                            onSuccess()
                        }
                    )
                } else {
                    // Error del Backend (ej: usuario ya existe)
                    val errorMsg = if (response.code() == 409) "El correo ya está registrado" else "Error del servidor: ${response.code()}"
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = errorMsg)
                }
            } catch (e: Exception) {
                // Si falla el servidor, intentamos solo registro LOCAL (Modo Offline opcional)
                // O simplemente mostramos error de conexión.
                // En este caso, optamos por mostrar error para asegurar consistencia.
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Error de conexión con servidor: ${e.message}")
            }
        }
    }
}

// Definición del estado de la UI para el registro
data class RegisterUiState(
    val nombres: String = "",
    val apellidos: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)
