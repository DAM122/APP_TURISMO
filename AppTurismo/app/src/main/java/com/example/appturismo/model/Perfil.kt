package com.example.appturismo.model

import com.google.gson.annotations.SerializedName

data class Perfil(
    @SerializedName("id") var id: Int,
    @SerializedName("rol") val rol: String,
)
