package com.inacap.iotmobileapp.utils

object ApiConfig {
    // IMPORTANTE: Obtener tu propia API key gratis en:
    // https://openweathermap.org/api
    // 1. Crear cuenta gratis
    // 2. Ir a "API keys" en tu perfil
    // 3. Copiar tu API key y reemplazar "TU_API_KEY_AQUI"

    const val OPENWEATHER_API_KEY = "ba8bcdc16e2294be50b7db7fe4e48ec0"

    // URL base de tu servidor Backend Node.js
    // IMPORTANTE: Usar 10.0.2.2 para emulador Android Studio (mapea a localhost de tu PC)
    const val BASE_URL_BACKEND = "http://10.0.2.2:3000/"

    // Ciudades de Chile disponibles
    const val CITY_LA_SERENA = "La Serena,CL"
    const val CITY_SANTIAGO = "Santiago,CL"
    const val CITY_VALPARAISO = "Valparaiso,CL"
    const val CITY_CONCEPCION = "Concepcion,CL"

    // Ciudad por defecto (La Serena para INACAP La Serena)
    const val DEFAULT_CITY = CITY_LA_SERENA
}
