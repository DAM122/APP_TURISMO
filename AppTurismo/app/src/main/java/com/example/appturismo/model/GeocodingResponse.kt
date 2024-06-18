package com.example.appturismo.model

data class GeocodingResponse(
    val results: List<Result>,
    val status: String
)