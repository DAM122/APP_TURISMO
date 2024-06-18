package com.example.appturismo.service

import com.example.appturismo.model.MonumentoArtistaResponse
import com.example.appturismo.model.MonumentoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface MonumentoService {
    @GET("monumentos")
    suspend fun getMonumentos(): Response<List<MonumentoResponse>>

    @GET("monumentos-with-artists")
    suspend fun getMonumentosWithArtists(): Response<List<MonumentoArtistaResponse>>

    @GET("monumentos/search")
    suspend fun getMonumentoByNombre(@Query("name") name: String): Response<MonumentoResponse>

    @GET("monumentos/monumentos/search")
    suspend fun searchMonumentosByNombre(@Query("name") nombre: String): Response<List<MonumentoResponse>>

    @GET("monumentos/localidad")
    suspend fun getMonumentosByLocalidadId(@Query("localidadId") localidadId: Long): Response<List<MonumentoResponse>>
}