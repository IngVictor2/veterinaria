package co.edu.iub.veterinaria.dto.pago

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal

data class PagoRequest(
    @field:NotNull val idFactura: Int,
    @field:NotNull val idMetodoPago: Int,
    @field:NotNull @field:Positive(message = "El monto debe ser mayor a cero") val monto: BigDecimal,
    val referenciaPago: String? = null
)
