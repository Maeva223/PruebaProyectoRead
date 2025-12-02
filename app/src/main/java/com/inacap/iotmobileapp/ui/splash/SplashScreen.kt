package com.inacap.iotmobileapp.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.inacap.iotmobileapp.R
import com.inacap.iotmobileapp.utils.Constants
import kotlinx.coroutines.delay

/**
 * Pantalla de Splash con animación
 * Redirige automáticamente al Login después de 3 segundos
 */
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit
) {
    // Cargar animación Lottie
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.splash_animation)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    // Navegar después del tiempo especificado
    LaunchedEffect(Unit) {
        delay(Constants.SPLASH_DURATION)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animación Lottie
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(300.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "IoT Mobile App",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "INACAP La Serena",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Instrucciones para personalizar la animación:
 *
 * 1. Descarga una animación gratis desde: https://lottiefiles.com/
 *    - Busca "technology", "IoT", "loading", etc.
 *    - Descarga el archivo JSON (Lottie JSON)
 *
 * 2. Reemplaza el archivo en:
 *    app/src/main/res/raw/splash_animation.json
 *
 * 3. Rebuild el proyecto y verás tu nueva animación!
 */
