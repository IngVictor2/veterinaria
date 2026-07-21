package co.edu.iub.veterinaria.dto.factura

import co.edu.iub.veterinaria.model.EstadoFactura
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class FacturaResponse(
    val idFactura: Int,
    val idCliente: Int,
    val nombreCliente: String,
    val fechaFactura: LocalDate,
    val subtotal: BigDecimal,
    val descuento: BigDecimal,
    val total: BigDecimal,
    val estadoFactura: EstadoFactura,
    val items: List<FacturaDetalleResponse>,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)

data class FacturaDetalleResponse(
    val idDetalle: Int,
    val idServicio: Int,
    val nombreServicio: String,
    val cantidad: Int,
    val precio: BigDecimal,
    val subtotal: BigDecimal
)
