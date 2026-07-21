package co.edu.iub.veterinaria.dto.cita

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ConsultaRequest(
    @field:NotNull val idCita: Int,
    val peso: BigDecimal? = null,
    val temperatura: BigDecimal? = null,
    @field:NotBlank val sintomas: String,
    val diagnosticoGeneral: String? = null,
    val tratamientoIndicado: String? = null,
    val observaciones: String? = null
)
