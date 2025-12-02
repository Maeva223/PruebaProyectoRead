package com.inacap.iotmobileapp.ui.recovery

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.inacap.iotmobileapp.ui.components.*

@Composable
fun RecoveryScreen(
    onNavigateBack: () -> Unit,
    onCodeVerified: (String, String) -> Unit,
    viewModel: RecoveryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "RECUPERAR CONTRASEÑA",
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
            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = "INGRESE EMAIL",
                keyboardType = KeyboardType.Email,
                isError = uiState.errorMessage.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.codeSent) {
                AppButton(
                    text = "RECUPERAR",
                    onClick = { viewModel.onSendCode() },
                    enabled = !uiState.isLoading
                )
            } else {
                Text("INGRESE CÓDIGO", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    value = uiState.code,
                    onValueChange = { viewModel.onCodeChange(it) },
                    label = "CÓDIGO",
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppButton(
                    text = "VERIFICAR",
                    onClick = { viewModel.onVerifyCode(onCodeVerified) },
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("59 Segundos", style = MaterialTheme.typography.bodySmall)
            }

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
