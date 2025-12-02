package com.inacap.iotmobileapp.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.inacap.iotmobileapp.R
import com.inacap.iotmobileapp.ui.components.*

/**
 * Pantalla de Login
 * Incluye opciones de: Ingresar, Registrarse y Recuperar Contraseña
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit,
    onNavigateToMainMenu: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onLogin = { viewModel.onLogin(onNavigateToMainMenu) },
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToRecovery = onNavigateToRecovery
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToRecovery: () -> Unit
) {
    // Determinar cuál animación mostrar según el estado
    val animationRes = when (uiState.animationState) {
        LoginAnimationState.IDLE -> R.raw.login_idle
        LoginAnimationState.PASSWORD_SHOW -> R.raw.login_password_show
        LoginAnimationState.PASSWORD_HIDE -> R.raw.login_password_hide
        LoginAnimationState.ERROR -> R.raw.login_error
        LoginAnimationState.SUCCESS -> R.raw.login_success
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animationRes))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = if (uiState.animationState == LoginAnimationState.IDLE)
            LottieConstants.IterateForever
        else 1,
        restartOnPlay = true
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animación Lottie interactiva
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bienvenido a mi APP",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de Email/Usuario
        AppTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = "INGRESE USUARIO",
            keyboardType = KeyboardType.Email,
            isError = uiState.errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Contraseña con botón de mostrar/ocultar
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("INGRESE CLAVE") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (uiState.passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff,
                        contentDescription = if (uiState.passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            isError = uiState.errorMessage.isNotEmpty(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Ingresar
        AppButton(
            text = "INGRESAR",
            onClick = onLogin,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Mensajes de error y éxito
        ErrorMessage(message = uiState.errorMessage)
        SuccessMessage(message = uiState.successMessage)

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace a Registro
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Registrarme",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Enlace a Recuperar Contraseña
        Text(
            text = "¿Olvidé mi Contraseña?",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onNavigateToRecovery() }
        )

        // Indicador de carga
        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLogin = {},
            onNavigateToRegister = {},
            onNavigateToRecovery = {}
        )
    }
}
