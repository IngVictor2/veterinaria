package co.edu.iub.veterinaria.dto.cita

import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalTime

data class CitaRequest(
    @field:NotNull val idMascota: Int,
    val idEmpleado: Int? = null,
    @field:NotNull val idServicio: Int,
    @field:NotNull val fechaCita: LocalDate,
    @field:NotNull val horaCita: LocalTime,
    val motivo: String? = null,
    val observaciones: String? = null
)
