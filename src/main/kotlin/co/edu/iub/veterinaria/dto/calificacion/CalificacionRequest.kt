package co.edu.iub.veterinaria.dto.calificacion

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class CalificacionRequest(
    @field:NotNull val idCita: Int,
    @field:Min(1) @field:Max(5) val puntuacion: Int,
    val comentario: String? = null
)
