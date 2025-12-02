package com.inacap.iotmobileapp.data.api

import com.inacap.iotmobileapp.utils.ApiConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // --- CLIENTE 1: Tu Backend Node.js ---
    private val backendRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL_BACKEND) // http://54.85.65.240/
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val backendApiService: BackendApiService by lazy {
        backendRetrofit.create(BackendApiService::class.java)
    }

    // --- CLIENTE 2: OpenWeatherMap (Clima Real) ---
    private val weatherRetrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherApiService: WeatherApiService by lazy {
        weatherRetrofit.create(WeatherApiService::class.java)
    }
}
