package com.example.retrofitcrudapp

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Alumnos: proyecto de la guía
    private const val BASE_URL_ALUMNOS = "https://66240d4504457d4aaf9b8530.mockapi.io/api/"
    // Profesores: tu propio proyecto de MockAPI
    private const val BASE_URL_PROFESORES = "https://6acad92f3115bcc9582110f6.mockapi.io/api/"

    // Muestra en Logcat (filtro "OkHttp") cada petición y respuesta
    private val client by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private fun crearRetrofit(baseUrl: String) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val instance: AlumnoApi by lazy {
        crearRetrofit(BASE_URL_ALUMNOS).create(AlumnoApi::class.java)
    }

    val profesorInstance: ProfesorApi by lazy {
        crearRetrofit(BASE_URL_PROFESORES).create(ProfesorApi::class.java)
    }
}