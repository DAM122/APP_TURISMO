package com.example.appturismo.service

import com.example.appturismo.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("maps/api/geocode/json")
    suspend fun getCoordinates(
        @Query("address") address: String,
        @Query("key") apiKey: String
    ): GeocodingResponse
}