package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.admin.AsignarPermisoRequest
import co.edu.iub.veterinaria.dto.admin.RolModuloResponse
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.RolModulo
import co.edu.iub.veterinaria.repository.ModuloRepository
import co.edu.iub.veterinaria.repository.RolModuloRepository
import co.edu.iub.veterinaria.repository.RolRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/permisos")
class ModuloPermisosController(
    private val rolModuloRepository: RolModuloRepository,
    private val rolRepository: RolRepository,
    private val moduloRepository: ModuloRepository
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
            return rolModuloRepository.save(existente).toResponse()
        }

        return rolModuloRepository.save(RolModulo().apply { this.rol = rol; this.modulo = modulo }).toResponse()
    }

    @DeleteMapping("/{idRol}/{idModulo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun revocar(@PathVariable idRol: Int, @PathVariable idModulo: Int) {
        val permiso = rolModuloRepository
            .findByRolIdRol(idRol)
            .firstOrNull { it.modulo.idModulo == idModulo }
            ?: throw ResourceNotFoundException("Permiso no encontrado")
        permiso.estado = false
        rolModuloRepository.save(permiso)
    }

    private fun RolModulo.toResponse() = RolModuloResponse(
        idRolModulo = idRolModulo!!,
        idRol = rol.idRol!!,
        idModulo = modulo.idModulo!!,
        nombreModulo = modulo.nombre,
        estado = estado
    )
}
