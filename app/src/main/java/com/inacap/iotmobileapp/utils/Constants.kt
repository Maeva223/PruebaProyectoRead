package com.inacap.iotmobileapp.utils

/**
 * Constantes de la aplicación
 */
object Constants {

    // Configuración de intentos de login
    const val MAX_LOGIN_ATTEMPTS = 3

    // Duración del splash screen en milisegundos
    const val SPLASH_DURATION = 3000L

    // Tiempo de expiración del código de recuperación en milisegundos
    const val RECOVERY_CODE_EXPIRATION = 60000L // 1 minuto

    // Intervalo de actualización de sensores en milisegundos
    const val SENSOR_UPDATE_INTERVAL = 2000L // 2 segundos

    // Umbral de temperatura
    const val TEMPERATURE_THRESHOLD = 20.0

    // Base URL de la API de sensores
    const val SENSOR_API_BASE_URL = "https://api.openweathermap.org/"
}
