package com.example.appturismo.model

import com.google.gson.annotations.SerializedName

data class ComentarioResponse (
    @SerializedName("id")
    val id:Long? = null,
    @SerializedName("usuario")
    val usuario:UsuarioResponse,
    @SerializedName("monumento")
    val monumento:MonumentoResponse,
    @SerializedName("mensaje")
    val mensaje: String,
    @SerializedName("puntuacion")
    val puntuacion: Double
)