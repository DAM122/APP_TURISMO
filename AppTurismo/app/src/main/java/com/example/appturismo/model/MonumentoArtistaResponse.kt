package com.example.appturismo.model

import com.google.gson.annotations.SerializedName

data class MonumentoArtistaResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("monumento")
    val monumento: MonumentoResponse,
    @SerializedName("artista")
    val artista: ArtistaResponse,
    @SerializedName("funcion")
    val funcion: String
)