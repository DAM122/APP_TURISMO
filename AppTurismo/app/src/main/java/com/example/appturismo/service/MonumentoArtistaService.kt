package com.example.appturismo.service

import com.example.appturismo.model.MonumentoArtistaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MonumentoArtistaService {
    @GET("monumentos-artista")
    suspend fun getMonumentoArtista() : Response<List<MonumentoArtistaResponse>>

    @GET("monumentos-artistas/{id_monumeto}")
    suspend fun getMonumentoArtistaByMonumentoId(@Path("id_monumeto") id_monumeto: Long): Response<MonumentoArtistaResponse>
}