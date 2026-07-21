package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.calificacion.CalificacionRequest
import co.edu.iub.veterinaria.dto.calificacion.CalificacionResponse
import co.edu.iub.veterinaria.service.CalificacionService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/calificaciones")
class CalificacionController(
    private val calificacionService: CalificacionService
) {
    @GetMapping("/cliente/{idCliente}")
    fun listarPorCliente(@PathVariable idCliente: Int): List<CalificacionResponse> =
        calificacionService.listarPorCliente(idCliente)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registrar(@Valid @RequestBody request: CalificacionRequest): CalificacionResponse =
        calificacionService.registrar(request)
}
