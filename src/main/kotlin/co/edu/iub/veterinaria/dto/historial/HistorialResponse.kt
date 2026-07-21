package co.edu.iub.veterinaria.dto.historial

import co.edu.iub.veterinaria.model.TipoHistorial
import java.time.LocalDateTime

data class HistorialResponse(
    val idHistorial: Int,
    val idMascota: Int,
    val nombreMascota: String,
    val idCita: Int,
    val tipoHistorial: TipoHistorial,
    val resumen: String,
    val fechaRegistro: LocalDateTime,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
