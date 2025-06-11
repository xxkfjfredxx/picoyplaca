package com.fredrueda.picoplacacamera.network

import com.fredrueda.picoplacacamera.model.Restriccion
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("{ciudad}/{tipo}/{digito}")
    suspend fun obtenerHtmlRestriccion(
        @Path("ciudad") ciudad: String,
        @Path("tipo") tipo: String,
        @Path("digito") digito: String
    ): ResponseBody
}
