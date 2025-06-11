package com.fredrueda.picoplacacamera.model

data class Restriccion(
    val placa: String,
    val tieneRestriccion: Boolean,
    val mensaje: String,
    val ciudad: String,
    val imagen: String? = null,
)
