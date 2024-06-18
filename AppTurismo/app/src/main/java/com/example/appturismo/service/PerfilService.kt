package com.example.appturismo.service

import com.example.appturismo.model.Perfil
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PerfilService {
    @GET("perfiles/{id}")
    suspend fun getPerfilById(@Path("id") id: Long): Response<Perfil>
}