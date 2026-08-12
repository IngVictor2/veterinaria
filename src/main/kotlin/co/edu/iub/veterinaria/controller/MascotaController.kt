package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.mascota.MascotaRequest
import co.edu.iub.veterinaria.dto.mascota.MascotaResponse
import co.edu.iub.veterinaria.security.CurrentUserHelper
import co.edu.iub.veterinaria.service.MascotaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/mascotas")
class MascotaController(
    private val mascotaService: MascotaService,
    private val currentUserHelper: CurrentUserHelper
) {
    @GetMapping
    fun listar(authentication: Authentication): List<MascotaResponse> {
        val idClienteActual = currentUserHelper.getClienteIdContextual(authentication)
        return if (idClienteActual != null) {
            mascotaService.listarPorCliente(idClienteActual)
        } else {
            mascotaService.listar()
        }
    }

    @GetMapping("/mis-mascotas")
    fun misMascotas(authentication: Authentication): List<MascotaResponse> {
        val idCliente = currentUserHelper.getClienteIdOrNull(authentication) ?: return emptyList()
        return mascotaService.listarPorCliente(idCliente)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Int, authentication: Authentication): MascotaResponse {
        val idCliente = currentUserHelper.getClienteIdContextual(authentication)
        return mascotaService.buscarPorId(id, idCliente)
    }

    @GetMapping("/cliente/{idCliente}")
    fun listarPorCliente(@PathVariable idCliente: Int, authentication: Authentication): List<MascotaResponse> {
        val idClienteActual = currentUserHelper.getClienteIdContextual(authentication)
        if (idClienteActual != null && idClienteActual != idCliente) {
            throw AccessDeniedException("No tiene permisos para ver estas mascotas")
        }
        return mascotaService.listarPorCliente(idCliente)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: MascotaRequest, authentication: Authentication): MascotaResponse =
        mascotaService.crear(request, currentUserHelper.getClienteIdContextual(authentication))

    @PutMapping("/{id}")
    fun actualizar(
        @PathVariable id: Int,
        @Valid @RequestBody request: MascotaRequest,
        authentication: Authentication
    ): MascotaResponse =
        mascotaService.actualizar(id, request, currentUserHelper.getClienteIdContextual(authentication))

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Int) = mascotaService.eliminar(id)
}
