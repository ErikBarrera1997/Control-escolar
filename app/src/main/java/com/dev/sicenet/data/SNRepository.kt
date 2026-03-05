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
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


/**
 * Repository that fetch.
 */
interface SNRepository {
    suspend fun acceso(m: String, p: String): String
    suspend fun accesoObjeto(m: String, p: String): Usuario
    suspend fun profile(m: String, p: String): ProfileStudent

    suspend fun calificacionesFinales(modEducativo: Int): List<CalificacionFinal>
    suspend fun calificacionesUnidades(): List<CalificacionUnidad>
    suspend fun cardex(lineamiento: Int): List<Kardex>
    suspend fun cargaAcademica(): List<CargaAcademica>

    // Nuevos métodos para local
    suspend fun saveProfile(profile: ProfileStudent)
    suspend fun getProfileLocal(m: String): ProfileStudent?
    suspend fun saveUsuario(usuario: Usuario)
    suspend fun getUsuarioLocal(m: String): Usuario?

}

class NetworSNRepository(
    private val snApiService: SICENETWService
) : SNRepository {

    override suspend fun acceso(m: String, p: String): String {
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

        //Log.d("SOAP_REQUEST", xml)

        val soapBody = xml.toRequestBody("text/xml; charset=utf-8".toMediaType())

        // Primera petición AQUI SE USA LA APIIII
        var res = snApiService.acceso(soapBody)
        var responseString = res.string()

        //Log.d("SOAP_RESPONSE", responseString)

        // Si la respuesta es HTML (página de ayuda), reintentar
        if (responseString.contains("<html")) {
            Log.w("SOAP_RETRY", "Respuesta HTML detectada, reintentando...")
            res = snApiService.acceso(soapBody)
            responseString = res.string()
            //Log.d("SOAP_RESPONSE_RETRY", responseString)
        }

        // Extraer contenido de <accesoLoginResult>
        val regex = "<accesoLoginResult>(.*?)</accesoLoginResult>".toRegex()
        val match = regex.find(responseString)
        val rawResult = match?.groups?.get(1)?.value ?: ""

        if (rawResult.isBlank()) {
            Log.w("SOAP_AUTH", "Respuesta vacía")
            return ""
        }

        // Parsear JSON dentro de accesoLoginResult
        return try {
            val json = JSONObject(rawResult)
            val acceso = json.optBoolean("acceso", false)
            if (acceso) {
                // Autenticación exitosa → devolver JSON completo o token
                rawResult
            } else {
                Log.w("SOAP_AUTH", "Credenciales inválidas: $rawResult")
                "" // devolver vacío para que el ViewModel muestre error
            }
        } catch (e: Exception) {
            Log.e("SOAP_AUTH", "Error parseando JSON: ${e.message}")
            ""
        }
    }

    private fun escapeXml(value: String): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    override suspend fun profile(m: String, p: String): ProfileStudent {
        val xml = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                   xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAlumnoAcademico xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
    """.trimIndent()

        val soapBody = xml.toRequestBody("text/xml; charset=utf-8".toMediaType())
        val res = snApiService.getAlumnoAcademico(soapBody)
        val responseString = res.string()

        val regex = "<getAlumnoAcademicoResult[^>]*>(.*?)</getAlumnoAcademicoResult>"
            .toRegex(setOf(RegexOption.DOT_MATCHES_ALL))
        val rawResult = regex.find(responseString)?.groups?.get(1)?.value ?: ""

        if (rawResult.isBlank()) {
            Log.e("SOAP_PROFILE", "No se encontró JSON en la respuesta: $responseString")
            throw JSONException("Respuesta inválida: bloque vacío")
        }

        val json = JSONObject(rawResult)

        return ProfileStudent(
            fechaReins = json.optString("fechaReins", ""),
            modEducativo = json.optInt("modEducativo", 0),
            adeudo = json.optBoolean("adeudo", false),
            urlFoto = json.optString("urlFoto", ""),
            adeudoDescripcion = json.optString("adeudoDescripcion", ""),
            inscrito = json.optBoolean("inscrito", false),
            estatus = json.optString("estatus", ""),
            semActual = json.optInt("semActual", 0),
            cdtosAcumulados = json.optInt("cdtosAcumulados", 0),
            cdtosActuales = json.optInt("cdtosActuales", 0),
            especialidad = json.optString("especialidad", ""),
            carrera = json.optString("carrera", ""),
            lineamiento = json.optInt("lineamiento", 0),
            nombre = json.optString("nombre", ""),
            matricula = json.optString("matricula", "")
        )
    }


    override suspend fun calificacionesFinales(modEducativo: Int): List<CalificacionFinal> {
        val xml = """
        <?xml version="1.0" encoding="utf-8"?>
        <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                       xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                       xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
              <bytModEducativo>$modEducativo</bytModEducativo>
            </getAllCalifFinalByAlumnos>
          </soap:Body>
        </soap:Envelope>
        """.trimIndent()

        val body = xml.toRequestBody("text/xml; charset=utf-8".toMediaType())
        val res = snApiService.getAllCalifFinalByAlumnos(body)
        val responseString = res.string()

        val regex = "<getAllCalifFinalByAlumnosResult[^>]*>(.*?)</getAllCalifFinalByAlumnosResult>" .toRegex(setOf(RegexOption.DOT_MATCHES_ALL))
        val rawResult = regex.find(responseString)?.groups?.get(1)?.value ?: ""

        Log.d("SOAP", "RawResult Finales: $rawResult")

        if (rawResult.isBlank()) return emptyList()

        val jsonArray = JSONArray(rawResult)
        val result = mutableListOf<CalificacionFinal>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(
                CalificacionFinal(
                    materia = obj.optString("materia", "N/A"),
                    calificacion = obj.optDouble("calif", 0.0),
                    periodo = obj.optString("grupo", "N/A"),
                    observaciones = obj.optString("Observaciones", "")
                )
            )
        }
        return result
    }


    override suspend fun calificacionesUnidades(): List<CalificacionUnidad> {
        val xml = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                   xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
    """.trimIndent()

        val body = xml.toRequestBody("text/xml; charset=utf-8".toMediaType())
        val res = snApiService.getCalifUnidadesByAlumno(body)
        val responseString = res.string()

        val regex = "<getCalifUnidadesByAlumnoResult[^>]*>(.*?)</getCalifUnidadesByAlumnoResult>"
            .toRegex(setOf(RegexOption.DOT_MATCHES_ALL))
        val rawResult = regex.find(responseString)?.groups?.get(1)?.value ?: ""

        Log.d("SOAP", "RawResult Unidades: $rawResult")

        if (rawResult.isBlank()) return emptyList()

        val jsonArray = try { JSONArray(rawResult) } catch (e: JSONException) {
            Log.e("SOAP", "Error parseando JSON Unidades: ${e.message}")
            return emptyList()
        }

        val result = mutableListOf<CalificacionUnidad>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val materia = obj.optString("Materia", "N/A")
            val grupo = obj.optString("Grupo", "N/A")
            val observaciones = obj.optString("Observaciones", "")
            for (u in 1..13) {
                val valor = obj.optString("C$u", null)
                if (!valor.isNullOrBlank()) {
                    result.add(
                        CalificacionUnidad(
                            materia = materia,
                            unidad = u,
                            calificacion = valor.toDoubleOrNull() ?: 0.0,
                            grupo = grupo,
                            observaciones = observaciones
                        )
                    )
                }
            }
        }
        return result
    }




    override suspend fun cardex(lineamiento: Int): List<Kardex> {
        val xml = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                   xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
          <aluLineamiento>$lineamiento</aluLineamiento>
        </getAllKardexConPromedioByAlumno>
      </soap:Body>
    </soap:Envelope>
    """.trimIndent()

        val body = xml.toRequestBody("text/xml; charset=utf-8".toMediaType())
        val res = snApiService.getAllKardexConPromedioByAlumno(body)
        val responseString = res.string()

        val regex = "<getAllKardexConPromedioByAlumnoResult[^>]*>(.*?)</getAllKardexConPromedioByAlumnoResult>"
            .toRegex(setOf(RegexOption.DOT_MATCHES_ALL))
        val rawResult = regex.find(responseString)?.groups?.get(1)?.value ?: ""

        if (rawResult.isBlank()) {
            Log.e("SOAP", "Respuesta vacía en kardex: $responseString")
            return emptyList()
        }

        val jsonArray = try { JSONArray(rawResult) } catch (e: JSONException) {
            Log.e("SOAP", "Error parseando JSON Kardex: ${e.message}")
            return emptyList()
        }

        val result = mutableListOf<Kardex>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            result.add(
                Kardex(
                    materia = obj.optString("materia", "N/A"),
                    calificacion = obj.optDouble("calif", 0.0),
                    periodo = obj.optString("periodo", "N/A"),
                    promedio = obj.optDouble("promedio", 0.0)
                )
            )
        }
        return result
    }



    override suspend fun cargaAcademica(): List<CargaAcademica> {
        val xml = """
    <?xml version="1.0" encoding="utf-8"?>
    <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                   xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                   xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
      <soap:Body>
        <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
      </soap:Body>
    </soap:Envelope>
    """.trimIndent()

        val body = xml.toRequestBody("text/xml; charset=utf-8".toMediaType())
        val res = snApiService.getCargaAcademicaByAlumno(body)
        val responseString = res.string()

        val regex = "<getCargaAcademicaByAlumnoResult[^>]*>(.*?)</getCargaAcademicaByAlumnoResult>"
            .toRegex(setOf(RegexOption.DOT_MATCHES_ALL))
        val rawResult = regex.find(responseString)?.groups?.get(1)?.value ?: ""

        if (rawResult.isBlank()) {
            Log.e("SOAP", "Respuesta vacía en cargaAcademica: $responseString")
            return emptyList()
        }

        val jsonArray = try { JSONArray(rawResult) } catch (e: JSONException) {
            Log.e("SOAP", "Error parseando JSON Carga: ${e.message}")
            return emptyList()
        }

        val result = mutableListOf<CargaAcademica>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val horarios = listOf(
                obj.optString("Lunes", ""),
                obj.optString("Martes", ""),
                obj.optString("Miercoles", ""),
                obj.optString("Jueves", ""),
                obj.optString("Viernes", ""),
                obj.optString("Sabado", "")
            ).filter { it.isNotBlank() }.joinToString(" | ")

            result.add(
                CargaAcademica(
                    materia = obj.optString("Materia", "N/A"),
                    profesor = obj.optString("Docente", "N/A"),
                    horario = horarios,
                    salon = obj.optString("Grupo", "N/A"),
                    observaciones = obj.optString("Observaciones", "")
                )
            )
        }
        return result
    }





    //'''''''''''''''''''''''''''''LOCAL''''''''''''''''''''''''''''''''''''''''''''''''
    override suspend fun saveProfile(profile: ProfileStudent) {

    }

    override suspend fun getProfileLocal(m: String): ProfileStudent? {
        return null
    }

    override suspend fun saveUsuario(usuario: Usuario) {

    }

    override suspend fun getUsuarioLocal(m: String): Usuario? {
        return null
    }


    override suspend fun accesoObjeto(m: String, p: String): Usuario {
        return Usuario(matricula = m)
    }




}





