package com.inacap.iotmobileapp.ui.users

import androidx.compose.runtime.Composable
import com.inacap.iotmobileapp.ui.register.RegisterScreen

@Composable
fun RegisterUserAdminScreen(onNavigateBack: () -> Unit) {
    RegisterScreen(
        onNavigateBack = onNavigateBack,
        onRegisterSuccess = onNavigateBack
    )
}
