package co.edu.iub.veterinaria.dto.cita

import co.edu.iub.veterinaria.model.EstadoCita
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class CitaResponse(
    val idCita: Int,
    val idMascota: Int,
    val nombreMascota: String,
    val idCliente: Int,
    val nombreCliente: String,
    val idEmpleado: Int,
    val nombreEmpleado: String,
    val idServicio: Int,
    val nombreServicio: String,
    val fechaCita: LocalDate,
    val horaCita: LocalTime,
    val motivo: String?,
    val estadoCita: EstadoCita,
    val observaciones: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
