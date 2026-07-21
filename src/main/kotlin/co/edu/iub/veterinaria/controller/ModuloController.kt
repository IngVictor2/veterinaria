package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.model.Modulo
import co.edu.iub.veterinaria.repository.ModuloRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/modulos")
class ModuloController(
    private val moduloRepository: ModuloRepository
) {
    @GetMapping
    fun listar(): List<ModuloResponse> =
        moduloRepository.findAll().map { it.toResponse() }

    private fun Modulo.toResponse() = ModuloResponse(
        idModulo = idModulo!!,
        nombre = nombre,
        descripcion = descripcion,
        estado = estado
    )
}

data class ModuloResponse(
    val idModulo: Int,
    val nombre: String,
    val descripcion: String?,
    val estado: Boolean
)
