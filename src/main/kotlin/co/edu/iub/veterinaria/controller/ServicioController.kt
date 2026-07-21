package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.servicio.ServicioRequest
import co.edu.iub.veterinaria.dto.servicio.ServicioResponse
import co.edu.iub.veterinaria.model.TipoServicio
import co.edu.iub.veterinaria.service.ServicioService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/servicios")
class ServicioController(
    private val servicioService: ServicioService
) {
    @GetMapping
    fun listar(@RequestParam(required = false) tipo: TipoServicio?): List<ServicioResponse> =
        if (tipo != null) servicioService.listarPorTipo(tipo) else servicioService.listar()

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Int): ServicioResponse = servicioService.buscarPorId(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: ServicioRequest): ServicioResponse =
        servicioService.crear(request)

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Int, @Valid @RequestBody request: ServicioRequest): ServicioResponse =
        servicioService.actualizar(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Int) = servicioService.eliminar(id)
}
