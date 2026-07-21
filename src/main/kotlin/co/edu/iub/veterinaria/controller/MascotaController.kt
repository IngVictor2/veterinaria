package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.mascota.MascotaRequest
import co.edu.iub.veterinaria.dto.mascota.MascotaResponse
import co.edu.iub.veterinaria.security.CurrentUserHelper
import co.edu.iub.veterinaria.service.MascotaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mascotas")
class MascotaController(
    private val mascotaService: MascotaService,
    private val currentUserHelper: CurrentUserHelper
) {
    @GetMapping
    fun listar(): List<MascotaResponse> = mascotaService.listar()

    @GetMapping("/mis-mascotas")
    fun misMascotas(authentication: Authentication): List<MascotaResponse> {
        val idCliente = currentUserHelper.getClienteIdOrNull(authentication) ?: return emptyList()
        return mascotaService.listarPorCliente(idCliente)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Int): MascotaResponse = mascotaService.buscarPorId(id)

    @GetMapping("/cliente/{idCliente}")
    fun listarPorCliente(@PathVariable idCliente: Int): List<MascotaResponse> =
        mascotaService.listarPorCliente(idCliente)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: MascotaRequest): MascotaResponse =
        mascotaService.crear(request)

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Int, @Valid @RequestBody request: MascotaRequest): MascotaResponse =
        mascotaService.actualizar(id, request)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Int) = mascotaService.eliminar(id)
}
