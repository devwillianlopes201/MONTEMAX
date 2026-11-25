package br.com.montemax.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Configura a comunicação do aplicativo com a API PHP.
object RetrofitClient {

    // Endereço do servidor local (XAMPP).
    private const val BASE_URL = "http://192.168.0.73/montemax/"

    // Cria a conexão com a API.
    val instancia: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Disponibiliza os serviços da API para o aplicativo.
    val apiService: ApiService by lazy {
        instancia.create(ApiService::class.java)
    }
}
