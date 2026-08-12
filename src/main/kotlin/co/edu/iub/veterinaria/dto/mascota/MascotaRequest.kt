package co.edu.iub.veterinaria.dto.mascota

import co.edu.iub.veterinaria.model.SexoMascota
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import java.math.BigDecimal
import java.time.LocalDate

data class MascotaRequest(
    @field:NotNull val idCliente: Int,
    @field:NotBlank val nombre: String,
    @field:NotBlank val especie: String,
    val raza: String? = null,
    @field:NotNull val sexo: SexoMascota,
    @field:PastOrPresent val fechaNacimiento: LocalDate? = null,
    @field:DecimalMin("0.01") @field:DecimalMax("500.00") val peso: BigDecimal? = null,
    val observaciones: String? = null
)
