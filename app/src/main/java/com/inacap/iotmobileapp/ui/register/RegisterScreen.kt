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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.ui.components.*

@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    // Usamos el ViewModel que está definido en RegisterViewModel.kt
    val viewModel: RegisterViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )
    val uiState by viewModel.uiState.collectAsState()

    RegisterScreenContent(
        uiState = uiState,
        onNombresChange = viewModel::onNombresChange,
        onApellidosChange = viewModel::onApellidosChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRegister = { viewModel.onRegister(onRegisterSuccess) },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onNombresChange: (String) -> Unit,
    onApellidosChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "REGISTRO DE USUARIO", onNavigateBack = onNavigateBack)
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
            AppTextField(value = uiState.nombres, onValueChange = onNombresChange, label = "INGRESE NOMBRES")
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(value = uiState.apellidos, onValueChange = onApellidosChange, label = "INGRESE APELLIDOS")
            Spacer(modifier = Modifier.height(12.dp))
            AppTextField(value = uiState.email, onValueChange = onEmailChange, label = "INGRESE E-MAIL", keyboardType = KeyboardType.Email)
            Spacer(modifier = Modifier.height(12.dp))
            PasswordTextField(value = uiState.password, onValueChange = onPasswordChange, label = "INGRESE CLAVE")
            Spacer(modifier = Modifier.height(12.dp))
            PasswordTextField(value = uiState.confirmPassword, onValueChange = onConfirmPasswordChange, label = "REPETIR CLAVE")
            Spacer(modifier = Modifier.height(24.dp))
            AppButton(text = "REGISTRAR", onClick = onRegister, enabled = !uiState.isLoading)
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(message = uiState.errorMessage)
            SuccessMessage(message = uiState.successMessage)
            if (uiState.isLoading) { Spacer(modifier = Modifier.height(16.dp)); CircularProgressIndicator() }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(),
            onNombresChange = {},
            onApellidosChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onRegister = {},
            onNavigateBack = {}
        )
    }
}
