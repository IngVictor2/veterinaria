package co.edu.iub.veterinaria.dto.mascota

import co.edu.iub.veterinaria.model.SexoMascota
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class MascotaResponse(
    val idMascota: Int,
    val idCliente: Int,
    val nombreCliente: String,
    val nombre: String,
    val especie: String,
    val raza: String?,
    val sexo: SexoMascota,
    val fechaNacimiento: LocalDate?,
    val peso: BigDecimal?,
    val observaciones: String?,
    val estado: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
