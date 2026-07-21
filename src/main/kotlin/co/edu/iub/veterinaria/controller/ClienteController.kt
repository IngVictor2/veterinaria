package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.cliente.ClienteProfileRequest
import co.edu.iub.veterinaria.dto.cliente.ClienteRequest
import co.edu.iub.veterinaria.dto.cliente.ClienteResponse
import co.edu.iub.veterinaria.service.ClienteService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
class ClienteController(

    private val clienteService: ClienteService

) {

    @GetMapping
    fun listar(): List<ClienteResponse> {
        return clienteService.listar()
    }

    @GetMapping("/me")
    fun miPerfil(authentication: Authentication): ClienteResponse {
        val idUsuario = authentication.principal as Int
        return clienteService.buscarPerfilPorUsuarioId(idUsuario)
    }

    @PutMapping("/me")
    fun actualizarMiPerfil(
        @Valid @RequestBody request: ClienteProfileRequest,
        authentication: Authentication
    ): ClienteResponse {
        val idUsuario = authentication.principal as Int
        return clienteService.actualizarPerfil(idUsuario, request)
    }

    @GetMapping("/{id}")
    fun buscarPorId(
        @PathVariable id: Int
    ): ClienteResponse {

        return clienteService.buscarPorId(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(
        @Valid @RequestBody request: ClienteRequest
    ): ClienteResponse {

        return clienteService.crear(request)
    }

    @PutMapping("/{id}")
    fun actualizar(
        @PathVariable id: Int,
        @Valid @RequestBody request: ClienteRequest
    ): ClienteResponse {

        return clienteService.actualizar(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(
        @PathVariable id: Int
    ) {

        clienteService.eliminar(id)
    }
}