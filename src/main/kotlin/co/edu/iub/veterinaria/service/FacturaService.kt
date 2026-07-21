package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.factura.FacturaDetalleResponse
import co.edu.iub.veterinaria.dto.factura.FacturaRequest
import co.edu.iub.veterinaria.dto.factura.FacturaResponse
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.*
import co.edu.iub.veterinaria.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class FacturaService(
    private val facturaRepository: FacturaRepository,
    private val detalleFacturaRepository: DetalleFacturaRepository,
    private val clienteRepository: ClienteRepository,
    private val citaRepository: CitaRepository,
    private val servicioRepository: ServicioRepository
) {

    @Transactional(readOnly = true)
    fun listarPorCliente(idCliente: Int): List<FacturaResponse> =
        facturaRepository.findByClienteIdCliente(idCliente).map { toResponse(it) }

    @Transactional(readOnly = true)
    fun buscarPorId(id: Int): FacturaResponse {
        val factura = facturaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Factura no encontrada") }
        return toResponse(factura)
    }

    @Transactional
    fun crear(request: FacturaRequest): FacturaResponse {
        val cliente = clienteRepository.findById(request.idCliente)
            .orElseThrow { ResourceNotFoundException("Cliente no encontrado") }

        val factura = Factura().apply {
            this.cliente = cliente
        }

        var subtotal = BigDecimal.ZERO
        val items = request.items.map { item ->
            val cita = citaRepository.findById(item.idCita)
                .orElseThrow { ResourceNotFoundException("Cita no encontrada") }
            val servicio = servicioRepository.findById(item.idServicio)
                .orElseThrow { ResourceNotFoundException("Servicio no encontrado") }

            val itemSubtotal = item.precio.multiply(BigDecimal.valueOf(item.cantidad.toLong()))
            subtotal = subtotal.add(itemSubtotal)

            DetalleFactura().apply {
                this.factura = factura
                this.cita = cita
                this.servicio = servicio
                cantidad = item.cantidad
                precio = item.precio
            }
        }

        val descuentoValido = if (request.descuento > subtotal) subtotal else request.descuento
        factura.subtotal = subtotal
        factura.descuento = descuentoValido
        factura.total = subtotal.subtract(descuentoValido)
        facturaRepository.save(factura)

        items.forEach { detalleFacturaRepository.save(it) }

        return toResponse(factura)
    }

    @Transactional(readOnly = true)
    fun listar(): List<FacturaResponse> =
        facturaRepository.findAll().map { toResponse(it) }

    private val transicionesFactura = mapOf(
        EstadoFactura.PENDIENTE to setOf(EstadoFactura.PAGADA, EstadoFactura.ANULADA),
        EstadoFactura.PAGADA to setOf(EstadoFactura.ANULADA),
        EstadoFactura.ANULADA to emptySet()
    )

    @Transactional
    fun cambiarEstado(id: Int, nuevoEstado: EstadoFactura) {
        val factura = facturaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Factura no encontrada") }
        val permitidos = transicionesFactura[factura.estadoFactura]
            ?: throw IllegalArgumentException("Estado actual inválido: ${factura.estadoFactura}")
        if (nuevoEstado !in permitidos) {
            throw IllegalArgumentException(
                "No se puede cambiar de ${factura.estadoFactura} a $nuevoEstado. " +
                "Transiciones permitidas: ${permitidos.joinToString(", ")}"
            )
        }
        factura.estadoFactura = nuevoEstado
        facturaRepository.save(factura)
    }

    private fun toResponse(f: Factura): FacturaResponse {
        val detalles = detalleFacturaRepository.findByFacturaIdFactura(f.idFactura!!)

        return FacturaResponse(
            idFactura = f.idFactura!!,
            idCliente = f.cliente.idCliente!!,
            nombreCliente = "${f.cliente.primerNombre} ${f.cliente.primerApellido}",
            fechaFactura = f.fechaFactura,
            subtotal = f.subtotal,
            descuento = f.descuento,
            total = f.total,
            estadoFactura = f.estadoFactura,
            items = detalles.map { d ->
                FacturaDetalleResponse(
                    idDetalle = d.idDetalleFactura!!,
                    idServicio = d.servicio.idServicio!!,
                    nombreServicio = d.servicio.nombre,
                    cantidad = d.cantidad,
                    precio = d.precio,
                    subtotal = d.precio.multiply(BigDecimal.valueOf(d.cantidad.toLong()))
                )
            },
            createdAt = f.createdAt,
            updatedAt = f.updatedAt
        )
    }
}
