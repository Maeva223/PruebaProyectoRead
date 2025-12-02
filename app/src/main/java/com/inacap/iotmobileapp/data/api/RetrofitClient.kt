package com.inacap.iotmobileapp.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // URL base de OpenWeatherMap API
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val sensorApiService: SensorApiService by lazy {
        retrofit.create(SensorApiService::class.java)
    }
}
