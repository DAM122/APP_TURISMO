package com.example.appturismo.model

import com.google.gson.annotations.SerializedName

data class ArtistaResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("nombre")
    val nombre: String,
    @SerializedName("apellido1")
    val apellido1: String,
    @SerializedName("apellido2")
    val apellido2: String,
    @SerializedName("profesion")
    val profesion: String,
    @SerializedName("fechaNacimiento")
    val fechaNacimiento: String?,
    @SerializedName("fechaMuerte")
    val fechaMuerte: String?,
    @SerializedName("procedencia")
    val procedencia: String,
    @SerializedName("paisOrigen")
    val paisOrigen: String,
    @SerializedName("historia")
    val historia: String
)