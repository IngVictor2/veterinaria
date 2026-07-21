package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.admin.AsignarRolRequest
import co.edu.iub.veterinaria.dto.admin.UsuarioRolResponse
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.UsuarioRol
import co.edu.iub.veterinaria.repository.RolRepository
import co.edu.iub.veterinaria.repository.UsuarioRepository
import co.edu.iub.veterinaria.repository.UsuarioRolRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/usuario-roles")
class RoleAssignmentController(
    private val usuarioRolRepository: UsuarioRolRepository,
    private val usuarioRepository: UsuarioRepository,
    private val rolRepository: RolRepository
) {
    @GetMapping("/usuario/{idUsuario}")
    fun listarRoles(@PathVariable idUsuario: Int): List<UsuarioRolResponse> =
        usuarioRolRepository.findByUsuarioIdUsuario(idUsuario).map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun asignar(@Valid @RequestBody body: AsignarRolRequest): UsuarioRolResponse {
        val idUsuario = body.idUsuario
        val idRol = body.idRol
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado") }
        val rol = rolRepository.findById(idRol)
            .orElseThrow { ResourceNotFoundException("Rol no encontrado") }

        if (usuarioRolRepository.existsByUsuarioIdUsuarioAndRolIdRol(idUsuario, idRol)) {
            val existente = usuarioRolRepository
                .findByUsuarioIdUsuario(idUsuario)
                .firstOrNull { it.rol.idRol == idRol }
                ?: throw ResourceNotFoundException("Asignación previa no encontrada")
            existente.estado = true
            return usuarioRolRepository.save(existente).toResponse()
        }

        return usuarioRolRepository.save(UsuarioRol().apply {
            this.usuario = usuario
            this.rol = rol
        }).toResponse()
    }

    @DeleteMapping("/{idUsuario}/{idRol}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun revocar(@PathVariable idUsuario: Int, @PathVariable idRol: Int) {
        val asignacion = usuarioRolRepository
            .findByUsuarioIdUsuario(idUsuario)
            .firstOrNull { it.rol.idRol == idRol }
            ?: throw ResourceNotFoundException("Asignación no encontrada")
        asignacion.estado = false
        usuarioRolRepository.save(asignacion)
    }

    private fun UsuarioRol.toResponse() = UsuarioRolResponse(
        idUsuarioRol = idUsuarioRol!!,
        idUsuario = usuario.idUsuario!!,
        idRol = rol.idRol!!,
        nombreRol = rol.nombre,
        estado = estado
    )
}
