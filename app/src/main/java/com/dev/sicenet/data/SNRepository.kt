/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dev.sicenet.data

import android.util.Log
import com.dev.sicenet.model.ProfileStudent
import com.example.marsphotos.model.Usuario
import com.dev.sicenet.network.SICENETWService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


/**
 * Repository that fetch.
 */
interface SNRepository {
    suspend fun acceso(m: String, p: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario
    suspend fun profile(m: String, p: String): ProfileStudent
}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    override suspend fun acceso(m: String, p: String): String {
        // Construir el XML con namespace correcto
        val xml = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                           xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                           xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">

              <soap:Body>
                <accesoLogin xmlns="http://tempuri.org/">
                  <strMatricula>$m</strMatricula>
                  <strContrasenia>${escapeXml(p)}</strContrasenia>
                  <tipoUsuario>ALUMNO</tipoUsuario>
                </accesoLogin>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        // Log del XML completo
        Log.d("SOAP_REQUEST", xml)

        val soapBody = xml.toRequestBody("text/xml; charset=utf-8".toMediaType())

        // Primera petición
        var res = snApiService.acceso(soapBody)
        var responseString = res.string()

        // Log de la respuesta completa
        Log.d("SOAP_RESPONSE", responseString)

        // Si la respuesta es HTML (página de ayuda), reintentar una segunda vez
        if (responseString.contains("<html")) {
            Log.w("SOAP_RETRY", "Respuesta HTML detectada, reintentando...")
            res = snApiService.acceso(soapBody)
            responseString = res.string()
            Log.d("SOAP_RESPONSE_RETRY", responseString)
        }

        // Extraer el token con regex
        val regex = "<accesoLoginResult>(.*?)</accesoLoginResult>".toRegex()
        val match = regex.find(responseString)
        return match?.groups?.get(1)?.value ?: ""
    }

    private fun escapeXml(value: String): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    override suspend fun accesoObjeto(m: String, p: String): Usuario {
        return Usuario(matricula = m)
    }

    override suspend fun profile(m: String, p: String): ProfileStudent {
        return ProfileStudent("Sin datos")
    }
}




