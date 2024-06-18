package com.example.appturismo.service

import com.example.appturismo.model.ComentarioResponse
import com.example.appturismo.model.MediaRespones
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface ComentarioService {
    @GET("comentarios")
    suspend fun getComentarios():Response<List<ComentarioResponse>>

    @POST("comentarios")
    suspend fun guardarComentario(@Body nuevoComentario: ComentarioResponse): Response<ComentarioResponse>

    @GET("comentarios/usuario/{idUsuario}")
    suspend fun getComentariosPorUsuario(@Path("idUsuario") idUsuario: Long): Response<List<ComentarioResponse>>

    @GET("comentarios/media-puntuacion")
    suspend fun getMediaPuntuacionComentarios(): Response<List<Array<Any>>>
}