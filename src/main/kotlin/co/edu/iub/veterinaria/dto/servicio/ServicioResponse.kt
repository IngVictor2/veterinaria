package co.edu.iub.veterinaria.dto.servicio

import co.edu.iub.veterinaria.model.TipoServicio
import java.math.BigDecimal
import java.time.LocalDateTime

data class ServicioResponse(
    val idServicio: Int,
    val nombre: String,
    val descripcion: String?,
    val tipoServicio: TipoServicio,
    val precio: BigDecimal,
    val estado: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
