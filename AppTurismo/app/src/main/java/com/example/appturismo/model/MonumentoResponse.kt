package com.example.appturismo.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class MonumentoResponse(
    @SerializedName("id") var id:Long,
    @SerializedName("localidad") var localidad:Localidad,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("fecha") val fecha: String?,
    @SerializedName("estilo") val estilo: String?,
    @SerializedName("descripcion") val descripcion: String,
    @SerializedName("visitable") val visitable: Boolean,
    @SerializedName("parking") val parking: Boolean,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("imagen") val imagen: String,
    @SerializedName("valoracion") val valoracion: Double
): Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        Localidad(parcel),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        localidad.writeToParcel(parcel, flags)
        parcel.writeString(nombre)
        parcel.writeString(fecha)
        parcel.writeString(estilo)
        parcel.writeString(descripcion)
        parcel.writeByte(if (visitable) 1 else 0)
        parcel.writeByte(if (parking) 1 else 0)
        parcel.writeString(telefono)
        parcel.writeString(imagen)
        parcel.writeDouble(valoracion)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MonumentoResponse> {
        override fun createFromParcel(parcel: Parcel): MonumentoResponse {
            return MonumentoResponse(parcel)
        }

        override fun newArray(size: Int): Array<MonumentoResponse?> {
            return arrayOfNulls(size)
        }

        private fun readLocalidadFromParcel(parcel: Parcel): Localidad {
            val id = parcel.readLong()
            val nombre = parcel.readString() ?: ""
            val provincia = parcel.readString() ?: ""
            val imagen = parcel.readString() ?: ""
            return Localidad(id, nombre, provincia,imagen)
        }
    }
}