package com.example.appturismo.model

import android.os.Parcel
import android.os.Parcelable

data class Localidad(
    val id: Long,
    val nombre: String,
    val provincia: String,
    val imagen: String
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

    companion object CREATOR : Parcelable.Creator<Localidad> {
        override fun createFromParcel(parcel: Parcel): Localidad {
            return Localidad(parcel)
        }

        override fun newArray(size: Int): Array<Localidad?> {
            return arrayOfNulls(size)
        }
    }
}