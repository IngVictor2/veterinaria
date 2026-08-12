package co.edu.iub.veterinaria.dto.cita

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ConsultaRequest(
    @field:NotNull val idCita: Int,
    @field:DecimalMin("0.01") @field:DecimalMax("500.00") val peso: BigDecimal? = null,
    @field:DecimalMin("30.00") @field:DecimalMax("45.00") val temperatura: BigDecimal? = null,
    @field:NotBlank val sintomas: String,
    val diagnosticoGeneral: String? = null,
    val tratamientoIndicado: String? = null,
    val observaciones: String? = null
)
