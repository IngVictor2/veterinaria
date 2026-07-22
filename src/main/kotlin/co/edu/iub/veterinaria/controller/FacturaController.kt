package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.exception.InvalidRequestException
import co.edu.iub.veterinaria.dto.factura.FacturaRequest
import co.edu.iub.veterinaria.dto.factura.FacturaResponse
import co.edu.iub.veterinaria.model.EstadoFactura
import co.edu.iub.veterinaria.security.CurrentUserHelper
import co.edu.iub.veterinaria.service.FacturaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/facturas")
class FacturaController(
    private val facturaService: FacturaService,
    private val currentUserHelper: CurrentUserHelper
) {
    @GetMapping
    fun listar(): List<FacturaResponse> = facturaService.listar()

    @GetMapping("/mis-facturas")
    fun misFacturas(authentication: Authentication): List<FacturaResponse> {
        val idCliente = currentUserHelper.getClienteIdOrNull(authentication) ?: return emptyList()
        return facturaService.listarPorCliente(idCliente)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Int): FacturaResponse = facturaService.buscarPorId(id)

    @GetMapping("/cliente/{idCliente}")
    fun listarPorCliente(@PathVariable idCliente: Int): List<FacturaResponse> =
        facturaService.listarPorCliente(idCliente)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: FacturaRequest): FacturaResponse =
        facturaService.crear(request)

    @PatchMapping("/{id}/estado")
    fun cambiarEstado(@PathVariable id: Int, @RequestBody body: Map<String, String>) {
        val estadoStr = body["estadoFactura"] ?: throw InvalidRequestException("estadoFactura es requerido")
        val estado = EstadoFactura.valueOf(estadoStr)
        facturaService.cambiarEstado(id, estado)
    }
}
