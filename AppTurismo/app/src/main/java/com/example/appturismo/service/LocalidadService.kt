package com.example.appturismo.service

import com.example.appturismo.model.LocalidadResponse
import retrofit2.Response
import retrofit2.http.GET

interface LocalidadService {
    @GET("localidades")
    suspend fun getLocalidades(): Response<List<LocalidadResponse>>
}