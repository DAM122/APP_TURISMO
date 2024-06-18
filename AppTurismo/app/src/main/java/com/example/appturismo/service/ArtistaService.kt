package com.example.appturismo.service

import com.example.appturismo.model.ArtistaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ArtistaService {
    @GET("artistas")
    suspend fun getArtistas(): Response<List<ArtistaResponse>>
    @GET("artistas/{id}")
    suspend fun getArtistaById(@Path("id") id: Long): Response<ArtistaResponse>
}