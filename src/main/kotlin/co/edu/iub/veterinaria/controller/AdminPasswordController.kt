package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.admin.AdminPasswordRequest
import co.edu.iub.veterinaria.dto.admin.UsuarioResponse
import co.edu.iub.veterinaria.repository.UsuarioRolRepository
import co.edu.iub.veterinaria.repository.UsuarioRepository
import co.edu.iub.veterinaria.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/usuarios")
class AdminPasswordController(
    private val authService: AuthService,
    private val usuarioRepository: UsuarioRepository,
    private val usuarioRolRepository: UsuarioRolRepository
) {
    @Transactional(readOnly = true)
    @GetMapping
    fun listar(): List<UsuarioResponse> =
        usuarioRepository.findAllConDetalle().map { u ->
            val idUsuario = u.idUsuario!!
            UsuarioResponse(
                idUsuario = idUsuario,
                nombreUsuario = u.nombreUsuario,
                correo = u.cliente?.correo ?: u.empleado?.correo,
                nombreCompleto = u.cliente?.let { "${it.primerNombre} ${it.primerApellido}" }
                    ?: u.empleado?.let { "${it.primerNombre} ${it.primerApellido}" }
                    ?: "Sin persona asociada",
                tipoCuenta = if (u.empleado != null) "EMPLEADO" else "CLIENTE",
                idCliente = u.cliente?.idCliente,
                idEmpleado = u.empleado?.idEmpleado,
                cargo = u.empleado?.cargo?.nombre,
                roles = usuarioRolRepository.findByUsuarioIdUsuarioAndEstadoTrue(idUsuario)
                    .map { it.rol.nombre },
                estado = u.estado,
                createdAt = u.createdAt,
                updatedAt = u.updatedAt
            )
        }

    @PostMapping("/{idUsuario}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cambiarPassword(@PathVariable idUsuario: Int, @Valid @RequestBody request: AdminPasswordRequest) {
        authService.cambiarPasswordAdmin(idUsuario, request.nuevaPassword)
    }
}