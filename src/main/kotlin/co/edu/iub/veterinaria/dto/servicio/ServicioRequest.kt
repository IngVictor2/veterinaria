package co.edu.iub.veterinaria.dto.servicio

import co.edu.iub.veterinaria.model.TipoServicio
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class ServicioRequest(
    @field:NotBlank val nombre: String,
    val descripcion: String? = null,
    @field:NotNull val tipoServicio: TipoServicio,
    @field:NotNull val precio: BigDecimal
)
