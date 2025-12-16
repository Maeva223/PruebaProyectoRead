package com.inacap.iotmobileapp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.inacap.iotmobileapp.data.api.LoginRequest
import com.inacap.iotmobileapp.data.api.RetrofitClient
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.database.entities.User
import com.inacap.iotmobileapp.data.repository.UserRepository
import com.inacap.iotmobileapp.utils.Constants
import com.inacap.iotmobileapp.utils.UserSession
import com.inacap.iotmobileapp.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Login
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(
        database.userDao(),
        database.recoveryCodeDao()
    )
    
    // Cliente para conectar con el Backend Node.js
    private val backendService = RetrofitClient.backendApiService

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            errorMessage = ""
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = ""
        )
    }

    fun onLogin(onSuccess: () -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        // Validaciones básicas
        if (email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Campos obligatorios vacíos")
            return
        }

        if (!Validators.isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Formato de email inválido")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            var backendSuccess = false
            var backendErrorMsg = ""

            // 1. Intentar Login en el Backend (Nube)
            try {
                val loginRequest = LoginRequest(email = email, password = password)
                val response = backendService.loginUser(loginRequest)

                if (response.isSuccessful && response.body()?.success == true) {
                    // Login en Nube EXITOSO
                    backendSuccess = true
                    val backendUser = response.body()?.user
                    val token = response.body()?.token ?: ""

                    // Sincronizar: Verificar si el usuario existe localmente, si no, crearlo
                    if (backendUser != null) {
                        var localUser = repository.getUserByEmail(email)
                        if (localUser == null) {
                            // Crear usuario local con datos del backend
                            val newUser = User(
                                nombres = backendUser.name,
                                apellidos = "",
                                email = backendUser.email,
                                password = password,
                                token = token,
                                rol = backendUser.rol ?: "OPERADOR",
                                estado = "ACTIVO",
                                id_departamento = backendUser.id_departamento
                            )
                            repository.registerUser(newUser)
                            localUser = repository.getUserByEmail(email)
                        } else {
                            // Actualizar token, rol e id_departamento del usuario local
                            localUser = localUser.copy(
                                token = token,
                                rol = backendUser.rol ?: localUser.rol,
                                id_departamento = backendUser.id_departamento ?: localUser.id_departamento,
                                estado = "ACTIVO"
                            )
                        }

                        // Guardar usuario completo en sesión (con token)
                        if (localUser != null) {
                            UserSession.login(localUser)
                        }
                    }

                    // Login exitoso
                    handleBackendLoginSuccess(email, onSuccess)
                    return@launch
                } else {
                    // El servidor respondió, pero fue error (ej: 401 Password incorrecta)
                    if (response.code() == 401) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Clave incorrecta (Servidor)"
                        )
                        triggerErrorAnimation()
                        return@launch // No intentamos local si el servidor dijo explícitamente que la clave está mal
                    } else if (response.code() == 404) {
                         // Usuario no existe en servidor
                         backendErrorMsg = "Usuario no encontrado en servidor"
                    } else {
                        backendErrorMsg = "Error servidor: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                // Error de conexión real (servidor apagado, sin internet)
                backendErrorMsg = "Sin conexión al servidor"
            }

            // 2. Si falló la conexión o el usuario no estaba en nube, intentamos Login Local (Offline)
            try {
                val user = repository.getUserByEmail(email)

                if (user == null) {
                    // Si no existe localmente y falló el backend
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = if(backendErrorMsg.isNotEmpty()) backendErrorMsg else "Credenciales inválidas (Local)"
                    )
                    triggerErrorAnimation()
                    return@launch
                }

                if (user.isBlocked) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Usuario bloqueado. Contacte al administrador"
                    )
                    return@launch
                }

                val loggedUser = repository.login(email, password)

                if (loggedUser != null) {
                    handleLocalLoginSuccess(email, onSuccess)
                } else {
                    // Existe el usuario local pero la clave está mal
                    handleLoginFailure(user, email)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error: ${e.message}"
                )
            }
        }
    }

    private suspend fun handleLocalLoginSuccess(email: String, onSuccess: () -> Unit) {
        repository.updateFailedAttempts(email, 0)
        val user = repository.getUserByEmail(email)
        if (user != null) {
            // ACTUALIZADO: Guardar usuario completo en sesión (incluye token, rol, departamento)
            UserSession.login(user)
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            successMessage = "Login correcto"
        )

        triggerSuccessAnimation()
        kotlinx.coroutines.delay(1500)
        onSuccess()
    }

    private suspend fun handleBackendLoginSuccess(email: String, onSuccess: () -> Unit) {
        // Similar a handleLocalLoginSuccess pero específico para backend
        repository.updateFailedAttempts(email, 0)

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            successMessage = "Login correcto (Backend)"
        )

        triggerSuccessAnimation()
        kotlinx.coroutines.delay(1500)
        onSuccess()
    }

    private suspend fun handleLoginFailure(user: User, email: String) {
        val failedAttempts = user.failedLoginAttempts + 1
        repository.updateFailedAttempts(email, failedAttempts)

        if (failedAttempts >= Constants.MAX_LOGIN_ATTEMPTS) {
            repository.updateBlockedStatus(email, true)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Usuario bloqueado por intentos"
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Clave incorrecta. Intentos: ${Constants.MAX_LOGIN_ATTEMPTS - failedAttempts}"
            )
        }
        triggerErrorAnimation()
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = "", successMessage = "")
    }

    fun setSuccessMessage(message: String) {
        _uiState.value = _uiState.value.copy(successMessage = message, errorMessage = "")
    }

    fun togglePasswordVisibility() {
        val newVisibility = !_uiState.value.passwordVisible
        _uiState.value = _uiState.value.copy(
            passwordVisible = newVisibility,
            animationState = if (newVisibility) LoginAnimationState.PASSWORD_SHOW else LoginAnimationState.PASSWORD_HIDE
        )
        viewModelScope.launch {
            kotlinx.coroutines.delay(700)
            if (_uiState.value.animationState == LoginAnimationState.PASSWORD_SHOW ||
                _uiState.value.animationState == LoginAnimationState.PASSWORD_HIDE) {
                _uiState.value = _uiState.value.copy(animationState = LoginAnimationState.IDLE)
            }
        }
    }

    private fun triggerErrorAnimation() {
        _uiState.value = _uiState.value.copy(animationState = LoginAnimationState.ERROR)
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000)
            _uiState.value = _uiState.value.copy(animationState = LoginAnimationState.IDLE)
        }
    }

    private fun triggerSuccessAnimation() {
        _uiState.value = _uiState.value.copy(animationState = LoginAnimationState.SUCCESS)
    }
}

enum class LoginAnimationState { IDLE, PASSWORD_SHOW, PASSWORD_HIDE, ERROR, SUCCESS }

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = "",
    val animationState: LoginAnimationState = LoginAnimationState.IDLE,
    val passwordVisible: Boolean = false
)
