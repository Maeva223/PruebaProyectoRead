package com.inacap.iotmobileapp.ui.users

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.inacap.iotmobileapp.ui.components.AppButton
import com.inacap.iotmobileapp.ui.components.AppTopBar
import com.inacap.iotmobileapp.ui.theme.IoTMobileAppTheme

@Composable
fun UserManagementScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToList: () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "GESTIÓN DE USUARIOS", onNavigateBack = onNavigateBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppButton(
                text = "INGRESAR USUARIOS",
                onClick = onNavigateToRegister
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                text = "LISTAR USUARIOS",
                onClick = onNavigateToList
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserManagementScreenPreview() {
    IoTMobileAppTheme {
        UserManagementScreen(
            onNavigateBack = {},
            onNavigateToRegister = {},
            onNavigateToList = {}
        )
    }
}
