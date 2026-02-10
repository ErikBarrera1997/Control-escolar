package com.dev.sicenet.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface SICENETWService {
    @POST("/ws/wsalumnos.asmx")
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    suspend fun acceso(@Body soap: RequestBody): ResponseBody
}

