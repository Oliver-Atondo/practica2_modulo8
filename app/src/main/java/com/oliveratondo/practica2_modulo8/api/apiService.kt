package com.oliveratondo.practica2_modulo8.api

import com.oliveratondo.practica2_modulo8.data.models.Instrument
import com.oliveratondo.practica2_modulo8.data.models.InstrumentPreview
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("instruments")
    suspend fun getInstruments(): List<InstrumentPreview>

    @GET("instruments/{id}")
    suspend fun getInstrumentDetails(
        @Path("id") id: Int
    ): Instrument
}
