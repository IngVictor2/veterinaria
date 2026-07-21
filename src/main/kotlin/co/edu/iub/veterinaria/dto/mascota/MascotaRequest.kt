package co.edu.iub.veterinaria.dto.mascota

import co.edu.iub.veterinaria.model.SexoMascota
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class MascotaRequest(
    @field:NotNull val idCliente: Int,
    @field:NotBlank val nombre: String,
    @field:NotBlank val especie: String,
    val raza: String? = null,
    @field:NotNull val sexo: SexoMascota,
    val fechaNacimiento: LocalDate? = null,
    val peso: BigDecimal? = null,
    val observaciones: String? = null
)
