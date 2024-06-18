package com.example.appturismo.model

import com.google.gson.annotations.SerializedName

data class MediaRespones(
    @SerializedName("monumento") val monumento: MonumentoResponse,
    @SerializedName("mediaPuntuacion") val mediaPuntuacion: Double
)