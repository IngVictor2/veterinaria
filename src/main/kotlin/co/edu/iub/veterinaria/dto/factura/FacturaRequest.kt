package co.edu.iub.veterinaria.dto.factura

import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class FacturaRequest(
    @field:NotNull val idCliente: Int,
    val descuento: BigDecimal = BigDecimal.ZERO,
    val items: List<FacturaItemRequest>
)

data class FacturaItemRequest(
    @field:NotNull val idCita: Int,
    @field:NotNull val idServicio: Int,
    val cantidad: Int = 1,
    @field:NotNull val precio: BigDecimal
)
