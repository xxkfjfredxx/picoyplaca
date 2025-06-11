package com.fredrueda.picoplacacamera.model

data class Perimetro(
    val nombre: String,
    val horario: String,
    val coordinates: List<List<Double>>
)

data class CiudadConPerimetros(
    val perimetros: List<Perimetro>
)

typealias DelimitacionesPorCiudad = Map<String, CiudadConPerimetros>
