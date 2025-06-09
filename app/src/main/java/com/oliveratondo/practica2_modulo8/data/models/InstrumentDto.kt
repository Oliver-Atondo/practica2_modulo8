package com.oliveratondo.practica2_modulo8.data.models

import com.google.gson.annotations.SerializedName

data class Location(
    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double
)

data class Instrument(
    @SerializedName("nombre")
    val name: String,
    @SerializedName("tipo")
    val type: String,
    @SerializedName("marca")
    val brand: String,
    @SerializedName("modelo")
    val model: String,
    @SerializedName("material")
    val material: String,
    @SerializedName("imagen")
    val image: String,
    @SerializedName("video")
    val video: String,
    @SerializedName("audio")
    val audio: String,
    @SerializedName("location")
    val location: Location
)

data class InstrumentPreview(
    @SerializedName("id")
    val id: String,
    @SerializedName("thumbnail")
    val previewImage: String,
    @SerializedName("nombre")
    val name: String,
    @SerializedName("location")
    val location: Location
)