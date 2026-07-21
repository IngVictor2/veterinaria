package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.pago.PagoRequest
import co.edu.iub.veterinaria.dto.pago.PagoResponse
import co.edu.iub.veterinaria.service.PagoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/pagos")
class PagoController(
    private val pagoService: PagoService
) {
    @GetMapping
    fun listar(): List<PagoResponse> = pagoService.listar()

    @GetMapping("/factura/{idFactura}")
    fun listarPorFactura(@PathVariable idFactura: Int): List<PagoResponse> =
        pagoService.listarPorFactura(idFactura)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registrar(@Valid @RequestBody request: PagoRequest): PagoResponse =
        pagoService.registrar(request)
}
