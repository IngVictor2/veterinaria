package co.edu.iub.veterinaria.dto.calificacion

import java.time.LocalDateTime

data class CalificacionResponse(
    val idCalificacion: Int,
    val idCita: Int,
    val idCliente: Int,
    val puntuacion: Int,
    val comentario: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
