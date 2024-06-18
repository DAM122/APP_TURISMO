package com.example.appturismo.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LocalidadResponse (
    @SerializedName("id") val id: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("provincia") val provincia: String,
    @SerializedName("imagen") val imagen: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(nombre)
        parcel.writeString(provincia)
        parcel.writeString(imagen)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LocalidadResponse> {
        override fun createFromParcel(parcel: Parcel): LocalidadResponse {
            return LocalidadResponse(parcel)
        }

        override fun newArray(size: Int): Array<LocalidadResponse?> {
            return arrayOfNulls(size)
        }
    }
}