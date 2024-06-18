package com.example.appturismo.service
import com.example.appturismo.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioService {
    @GET("usuarios")
    suspend fun getUsuarios(): Response<List<UsuarioResponse>>

    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Long): Response<UsuarioResponse>

    @GET("usuarios/authenticate")
    suspend fun authenticate(
        @Query("nickname") nickname: String,
        @Query("password") password: String
    ): Response<UsuarioResponse>

    @PUT("usuarios/{id}")
    suspend fun updateUsuario(
        @Path("id") id: Long,
        @Body usuarioDetails: UsuarioResponse
    ): Response<UsuarioResponse>

    @POST("usuarios")
    suspend fun createUsuario(@Body usuario: UsuarioResponse): Response<UsuarioResponse>
}
