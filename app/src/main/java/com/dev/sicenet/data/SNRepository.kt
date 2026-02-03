package com.dev.sicenet.data

import android.util.Log
import com.example.marsphotos.model.ProfileStudent
import com.example.marsphotos.model.Usuario
import com.dev.sicenet.network.SICENETWService
import com.dev.sicenet.network.bodyacceso
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

interface SNRepository {
    suspend fun acceso(m: String, p: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario
    suspend fun profile(m: String, p: String): ProfileStudent
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    override suspend fun acceso(m: String, p: String): String {
        return try {

            val passwordConRegla = ("$" + p).replace("$", "{$}")


            val soapBodyString = String.format(bodyacceso, m, escapeXml(passwordConRegla))
            val soapBody = soapBodyString.toRequestBody("text/xml; charset=utf-8".toMediaType())

            val res = snApiService.acceso(soapBody)
            val responseString = res.string()

            Log.d("SICENET_RAW", "Enviado Final: $passwordConRegla")
            Log.d("SICENET_RAW", "Respuesta del Servidor: $responseString")

            val regex = "<accesoLoginResult>(.*?)</accesoLoginResult>".toRegex()
            val match = regex.find(responseString)
            match?.groups?.get(1)?.value ?: "Respuesta vacía"
        } catch (e: Exception) {
            Log.e("SICENET_ERROR", "Fallo: ${e.message}")
            "Error de conexión"
        }
    }


    private fun escapeXml(value: String): String {
        return value.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario(matricula = m)
    override suspend fun profile(m: String, p: String): ProfileStudent = ProfileStudent(matricula = m)
}

class DBLocalSNRepository(val apiDB : Any): SNRepository {
    override suspend fun acceso(m: String, p: String): String = ""
    override suspend fun accesoObjeto(m: String, p: String): Usuario = Usuario(matricula = "")
    override suspend fun profile(m: String, p: String): ProfileStudent = ProfileStudent(matricula = "")
}