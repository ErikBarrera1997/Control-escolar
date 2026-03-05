package com.dev.sicenet.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://sicenet.surguanajuato.tecnm.mx/") //URL base real del servicio SOAP
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val service: SICENETWService = retrofit.create(SICENETWService::class.java)
}

