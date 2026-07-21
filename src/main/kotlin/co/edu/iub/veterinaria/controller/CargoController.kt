package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.model.Cargo
import co.edu.iub.veterinaria.repository.CargoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cargos")
class CargoController(
    private val cargoRepository: CargoRepository
) {
    @GetMapping
    fun listar(): List<CargoResponse> =
        cargoRepository.findAll().map { it.toResponse() }

    private fun Cargo.toResponse() = CargoResponse(
        idCargo = idCargo!!,
        nombre = nombre,
        descripcion = descripcion,
        estado = estado
    )
}

data class CargoResponse(
    val idCargo: Int,
    val nombre: String,
    val descripcion: String?,
    val estado: Boolean
)
