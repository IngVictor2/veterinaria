package co.edu.iub.veterinaria.dto.pago

import java.math.BigDecimal
import java.time.LocalDateTime

data class PagoResponse(
    val idPago: Int,
    val idFactura: Int,
    val idMetodoPago: Int,
    val nombreMetodoPago: String,
    val fechaPago: LocalDateTime,
    val monto: BigDecimal,
    val referenciaPago: String?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
