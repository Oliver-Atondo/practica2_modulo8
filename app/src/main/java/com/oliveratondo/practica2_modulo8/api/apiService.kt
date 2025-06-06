package com.oliveratondo.practica2_modulo8.api

import com.oliveratondo.practica2_modulo8.data.models.Hotel
import retrofit2.http.GET

interface ApiService {
    @GET("hoteles")
    suspend fun getHotels(): List<Hotel>
}
