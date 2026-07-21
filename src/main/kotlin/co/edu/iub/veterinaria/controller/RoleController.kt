package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.admin.RolRequest
import co.edu.iub.veterinaria.dto.admin.RolResponse
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.Rol
import co.edu.iub.veterinaria.repository.RolRepository
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/roles")
class RoleController(
    private val rolRepository: RolRepository
) {
    @GetMapping
    fun listar(): List<RolResponse> = rolRepository.findAll().map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: RolRequest): RolResponse {
        val rol = Rol().apply {
            nombre = request.nombre
            descripcion = request.descripcion
        }
        return rolRepository.save(rol).toResponse()
    }

    @PutMapping("/{id}")
    fun actualizar(@PathVariable id: Int, @Valid @RequestBody request: RolRequest): RolResponse {
        val existente = rolRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Rol no encontrado") }
        existente.nombre = request.nombre
        existente.descripcion = request.descripcion
        return rolRepository.save(existente).toResponse()
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun eliminar(@PathVariable id: Int) {
        val rol = rolRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Rol no encontrado") }
        rol.estado = false
        rolRepository.save(rol)
    }

    private fun Rol.toResponse() = RolResponse(
        idRol = idRol!!,
        nombre = nombre,
        descripcion = descripcion,
        estado = estado,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
