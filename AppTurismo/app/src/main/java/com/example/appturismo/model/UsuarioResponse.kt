package com.example.appturismo.model

import com.google.gson.annotations.SerializedName

data class UsuarioResponse (
    @SerializedName("id") var id:Long,
    @SerializedName("nombre") var nombre:String,
    @SerializedName("apellido1") var apellido1: String,
    @SerializedName("apellido2") var apellido2: String,
    @SerializedName("password") val password: String,
    @SerializedName("nickname") var nickname: String,
    @SerializedName("perfil") val perfil: Perfil
)