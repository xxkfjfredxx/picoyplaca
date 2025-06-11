package com.fredrueda.picoplacacamera.repository

import android.util.Log
import com.fredrueda.picoplacacamera.model.Restriccion
import com.fredrueda.picoplacacamera.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

class RestriccionRepository {

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.pyphoy.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    suspend fun verificarRestriccion(ciudad: String, tipo: String, placa: String): Restriccion {
        return withContext(Dispatchers.IO) {
            try {
                val tipoApi = when (tipo.lowercase(Locale.ROOT)) {
                    "carro" -> "particulares"
                    "moto" -> "motos"
                    else -> "particulares"
                }

                val ciudadApi = ciudad.lowercase(Locale.ROOT).removeAccents()
                val digito = obtenerUltimoDigitoNumerico(placa)

                Log.d("Restriccion", "Consultando $ciudadApi/$tipoApi/$digito")

                val response = apiService.obtenerHtmlRestriccion(ciudadApi, tipoApi, digito)
                if (!response.isSuccessful) {
                    if (response.code() == 404) {
                        return@withContext Restriccion(
                            placa = placa,
                            tieneRestriccion = false,
                            mensaje = "⚠️ No se encontró información de pico y placa para '$ciudad' con tipo de vehículo '$tipo'. Es posible que la ciudad no tenga restricciones registradas.",
                            ciudad = ciudad
                        )
                    } else {
                        throw HttpException(response)
                    }
                }
                val html = response.body()?.string() ?: throw Exception("Respuesta vacía.")

                return@withContext parsearRestriccionDesdeHtml(placa, ciudadApi, html)

            } catch (e: Exception) {
                Restriccion(
                    placa = placa,
                    tieneRestriccion = false,
                    mensaje = "Error al consultar la restricción: ${e.message}", ciudad
                )
            }
        }
    }

    fun parsearRestriccionDesdeHtml(placa: String, ciudad: String, html: String): Restriccion {
        val doc = Jsoup.parse(html)

        val divsAmarillos = doc.select("div.bg-yellow-400").map { it.text().trim() }
        val digitosRestringidos = if (divsAmarillos.size > 2) divsAmarillos[2] else ""
        val ultimoDigito = obtenerUltimoDigitoNumerico(placa)
        val listaDigitos = digitosRestringidos
            .split('-', '–', '—', ' ')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val estaRestringido = listaDigitos.contains(ultimoDigito)

        val mensaje = buildString {
            if (estaRestringido) {
                appendLine("🚫 Hoy hay restricción en $ciudad para placas terminadas en $digitosRestringidos. Tu placa $placa está restringida. 🚫")
            } else {
                appendLine("✅ La placa $placa NO tiene pico y placa hoy en $ciudad.")
            }
            appendLine()
        }

        val proximasFechas = doc.select("ul li a time")
            .mapNotNull { it.text() }
            .takeIf { it.isNotEmpty() }
            ?: listOf("No se encontraron próximas fechas.")
        val fechasFormateadas = proximasFechas.joinToString("\n") { "• $it" }

        fun extraerSeccion(titulo: String): String {
            val header = doc.select("*").firstOrNull {
                it.tagName().startsWith("h") && it.text().trim().equals(titulo, ignoreCase = true)
            }
            return header?.nextElementSibling()?.text()?.trim().orEmpty()
        }

        // ✅ NUEVA SECCIÓN: Observaciones con imagen y lista
        val observacionesHeader =
            doc.select("h2").firstOrNull { it.text().contains("Observaciones", ignoreCase = true) }
        val observacionesTexto = observacionesHeader?.nextElementSibling()?.text().orEmpty()
        val observacionesLista = observacionesHeader
            ?.nextElementSiblings()
            ?.firstOrNull { it.tagName() == "ol" }
            ?.select("li")
            ?.joinToString("\n") { "- ${it.text().trim()}" }
            ?: "No se encontraron observaciones."
        val imagenObservaciones = observacionesHeader?.nextElementSiblings()
            ?.firstOrNull { it.tagName() == "img" }
            ?.attr("src")
            ?.let { if (it.startsWith("/")) "https://www.pyphoy.com$it" else it }
            ?: ""

        val observacionesFinal = buildString {
            if (observacionesTexto.isNotBlank()) appendLine("📍 $observacionesTexto\n")
            if (observacionesLista.isNotBlank()) appendLine(observacionesLista)
            //if (imagenObservaciones.isNotBlank()) appendLine("\n🖼️ Imagen: $imagenObservaciones")
        }

        val tiposVehiculos = extraerSeccion("Tipos de vehículos")
        val excepciones = doc.select("*")
            .firstOrNull { it.text().trim().equals("Excepciones", ignoreCase = true) }
            ?.nextElementSiblings()
            ?.joinToString("\n") { "- ${it.text().trim()}" }
            ?.takeIf { it.isNotBlank() }
            ?: "No se encontraron excepciones."

        val detallesExtra = listOfNotNull(
            tiposVehiculos.takeIf { it.isNotBlank() }?.let { "🚗 Tipos de vehículos: $it" },
            observacionesFinal.takeIf { it.isNotBlank() },
            //excepciones.takeIf { it.isNotBlank() }?.let { "❗ Excepciones:\n$it" }
        ).joinToString("\n\n")

        return Restriccion(
            placa = placa,
            tieneRestriccion = estaRestringido,
            mensaje = buildString {
                append(mensaje)
                appendLine("📅 Prográmese:\n$fechasFormateadas")
                if (detallesExtra.isNotBlank()) appendLine("\n$detallesExtra")
            },
            ciudad = ciudad,
            imagen = imagenObservaciones
        )
    }


    private fun obtenerUltimoDigitoNumerico(placa: String): String {
        return placa.reversed().firstOrNull { it.isDigit() }?.toString()
            ?: throw IllegalArgumentException("La placa no contiene ningún dígito")
    }

    private fun String.removeAccents(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }


}
