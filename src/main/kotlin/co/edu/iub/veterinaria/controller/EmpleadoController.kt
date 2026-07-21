package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.empleado.EmpleadoRequest
import co.edu.iub.veterinaria.dto.empleado.EmpleadoResponse
import co.edu.iub.veterinaria.service.EmpleadoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/empleados")
class EmpleadoController(
    private val empleadoService: EmpleadoService
) {
    @GetMapping
    fun listar(): List<EmpleadoResponse> = empleadoService.listar()

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Int): EmpleadoResponse = empleadoService.buscarPorId(id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: EmpleadoRequest): EmpleadoResponse =
        empleadoService.crear(request)

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Int, @Valid @RequestBody request: EmpleadoRequest): EmpleadoResponse =
        empleadoService.actualizar(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Int) = empleadoService.eliminar(id)
}
