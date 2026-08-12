package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.admin.AsignarPermisoRequest
import co.edu.iub.veterinaria.dto.admin.RolModuloResponse
import co.edu.iub.veterinaria.exception.InvalidRequestException
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.RolModulo
import co.edu.iub.veterinaria.repository.ModuloRepository
import co.edu.iub.veterinaria.repository.RolModuloRepository
import co.edu.iub.veterinaria.repository.RolRepository
import co.edu.iub.veterinaria.security.ModuleAuthorizationManager
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/permisos")
class ModuloPermisosController(
    private val rolModuloRepository: RolModuloRepository,
    private val rolRepository: RolRepository,
    private val moduloRepository: ModuloRepository,
    private val moduleAuthorizationManager: ModuleAuthorizationManager
) {
    @GetMapping("/rol/{idRol}")
    fun listarPorRol(@PathVariable idRol: Int): List<RolModuloResponse> =
        rolModuloRepository.findByRolIdRol(idRol).map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun asignar(@Valid @RequestBody body: AsignarPermisoRequest): RolModuloResponse {
        val idRol = body.idRol
        val idModulo = body.idModulo
        val rol = rolRepository.findById(idRol)
            .orElseThrow { ResourceNotFoundException("Rol no encontrado") }
        val modulo = moduloRepository.findById(idModulo)
            .orElseThrow { ResourceNotFoundException("Módulo no encontrado") }

        if (rolModuloRepository.existsByRolIdRolAndModuloIdModulo(idRol, idModulo)) {
            val existente = rolModuloRepository
                .findByRolIdRol(idRol)
                .firstOrNull { it.modulo.idModulo == idModulo }
                ?: throw ResourceNotFoundException("Permiso previo no encontrado")
            existente.estado = true
            rolModuloRepository.save(existente)
            moduleAuthorizationManager.invalidar()
            return existente.toResponse()
        }

        val nuevo = rolModuloRepository.save(RolModulo().apply { this.rol = rol; this.modulo = modulo })
        moduleAuthorizationManager.invalidar()
        return nuevo.toResponse()
    }

    @DeleteMapping("/{idRol}/{idModulo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun revocar(@PathVariable idRol: Int, @PathVariable idModulo: Int) {
        val rol = rolRepository.findById(idRol)
            .orElseThrow { ResourceNotFoundException("Rol no encontrado") }
        if (rol.nombre.equals("ADMIN", ignoreCase = true)) {
            throw InvalidRequestException("No puedes quitar permisos al rol ADMIN")
        }
        val permiso = rolModuloRepository
            .findByRolIdRol(idRol)
            .firstOrNull { it.modulo.idModulo == idModulo }
            ?: throw ResourceNotFoundException("Permiso no encontrado")
        permiso.estado = false
        rolModuloRepository.save(permiso)
        moduleAuthorizationManager.invalidar()
    }

    private fun RolModulo.toResponse() = RolModuloResponse(
        idRolModulo = idRolModulo!!,
        idRol = rol.idRol!!,
        idModulo = modulo.idModulo!!,
        nombreModulo = modulo.nombre,
        estado = estado
    )
}
