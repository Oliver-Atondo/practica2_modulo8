package com.oliveratondo.practica2_modulo8.data.models

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)

data class Hotel(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("location")
    val location: Location
)
