package com.dev.sicenet.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
//AQUI ESTAN LOS HEADERS
interface SICENETWService {

    @POST("ws/wsalumnos.asmx")
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    suspend fun acceso(@Body soap: RequestBody): ResponseBody

    @POST("ws/wsalumnos.asmx")
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAlumnoAcademico\""
    )
    suspend fun getAlumnoAcademico(@Body soap: RequestBody): ResponseBody

    @POST("ws/wsalumnos.asmx")
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAllCalifFinalByAlumnos\""
    )
    suspend fun getAllCalifFinalByAlumnos(@Body body: RequestBody): ResponseBody

    @POST("ws/wsalumnos.asmx")
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getCalifUnidadesByAlumno\""
    )
    suspend fun getCalifUnidadesByAlumno(@Body body: RequestBody): ResponseBody

    @POST("ws/wsalumnos.asmx")
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAllKardexConPromedioByAlumno\""
    )
    suspend fun getAllKardexConPromedioByAlumno(@Body body: RequestBody): ResponseBody

    @POST("ws/wsalumnos.asmx")
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getCargaAcademicaByAlumno\""
    )
    suspend fun getCargaAcademicaByAlumno(@Body body: RequestBody): ResponseBody
}
