# Instrucciones de Desarrollo - IoT Mobile App

## 📱 Estado Actual del Proyecto

### ✅ Archivos ya creados y funcionalesSISTEMA BASE:
- `data/database/entities/User.kt` - Entidad de usuario
- `data/database/entities/RecoveryCode.kt` - Códigos de recuperación
- `data/database/dao/UserDao.kt` - DAO de usuarios
- `data/database/dao/RecoveryCodeDao.kt` - DAO de códigos
- `data/database/AppDatabase.kt` - Base de datos principal
- `data/repository/UserRepository.kt` - Repositorio
- `utils/Validators.kt` - Validaciones
- `utils/Constants.kt` - Constantes

NAVEGACIÓN:
- `ui/navigation/Screen.kt` - Rutas
- `ui/navigation/NavGraph.kt` - Grafo de navegación

PANTALLAS COMPLETADAS:
- `ui/splash/SplashScreen.kt`
- `ui/login/LoginScreen.kt` + LoginViewModel.kt
- `ui/recovery/RecoveryScreen.kt` + RecoveryViewModel.kt
- `ui/recovery/CreatePasswordScreen.kt` + CreatePasswordViewModel.kt

COMPONENTES:
- `ui/components/CommonComponents.kt` - Componentes reutilizables

---

## 🔧 PASO SIGUIENTE: Completar pantallas faltantes

### 1. REGISTER SCREEN (Pantalla 5 - Registro desde Login)

Crear en: `ui/register/RegisterScreen.kt`

```kotlin
package com.inacap.iotmobileapp.ui.register

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.data.database.AppDatabase
import com.inacap.iotmobileapp.data.database.entities.User
import com.inacap.iotmobileapp.data.repository.UserRepository
import com.inacap.iotmobileapp.ui.components.*
import com.inacap.iotmobileapp.utils.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: RegisterViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "REGISTRO DE USUARIO",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTextField(
                value = uiState.nombres,
                onValueChange = { viewModel.onNombresChange(it) },
                label = "INGRESE NOMBRES"
            )
            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = uiState.apellidos,
                onValueChange = { viewModel.onApellidosChange(it) },
                label = "INGRESE APELLIDOS"
            )
            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = "INGRESE E-MAIL",
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextField(
                value = uiState.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "INGRESE CLAVE"
            )
            Spacer(modifier = Modifier.height(12.dp))

            PasswordTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChange(it) },
                label = "REPETIR CLAVE"
            )
            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "REGISTRAR",
                onClick = { viewModel.onRegister(onRegisterSuccess) },
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = uiState.errorMessage)
            SuccessMessage(message = uiState.successMessage)

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = UserRepository(database.userDao(), database.recoveryCodeDao())

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onNombresChange(value: String) {
        _uiState.value = _uiState.value.copy(nombres = value, errorMessage = "")
    }

    fun onApellidosChange(value: String) {
        _uiState.value = _uiState.value.copy(apellidos = value, errorMessage = "")
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = "")
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = "")
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = "")
    }

    fun onRegister(onSuccess: () -> Unit) {
        val nombres = _uiState.value.nombres.trim()
        val apellidos = _uiState.value.apellidos.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        // Validaciones
        if (nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() || password.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Campos obligatorios vacíos")
            return
        }

        if (!Validators.isValidName(nombres) || !Validators.isValidName(apellidos)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Nombres y apellidos solo deben contener letras")
            return
        }

        if (!Validators.isValidEmail(email)) {
            _uiState.value = _uiState.value.copy(errorMessage = "Formato de email inválido")
            return
        }

        if (!Validators.isValidPassword(password)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Contraseña débil. ${Validators.getPasswordErrorMessage(password)}"
            )
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Contraseñas no coinciden")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            val user = User(
                nombres = nombres,
                apellidos = apellidos,
                email = email,
                password = password
            )

            val result = repository.registerUser(user)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Registro exitoso"
                    )
                    kotlinx.coroutines.delay(1500)
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Error al registrar"
                    )
                }
            )
        }
    }
}

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
```

---

### 2. MAIN MENU (Pantalla 6 - Menú Principal)

Crear en: `ui/menu/MainMenuScreen.kt`

**Ver el archivo completo que te voy a crear a continuación...**

