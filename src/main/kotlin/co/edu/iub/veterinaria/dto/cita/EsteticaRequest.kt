package co.edu.iub.veterinaria.dto.cita

import jakarta.validation.constraints.NotNull

data class EsteticaRequest(
    @field:NotNull val idCita: Int,
    val detalles: String? = null,
    val observaciones: String? = null
)
