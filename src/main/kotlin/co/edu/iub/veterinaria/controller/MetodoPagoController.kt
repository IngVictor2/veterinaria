package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.model.MetodoPago
import co.edu.iub.veterinaria.repository.MetodoPagoRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/metodos-pago")
class MetodoPagoController(
    private val metodoPagoRepository: MetodoPagoRepository
) {
    @GetMapping
    fun listar(): List<MetodoPagoResponse> =
        metodoPagoRepository.findAll().map { it.toResponse() }

    private fun MetodoPago.toResponse() = MetodoPagoResponse(
        idMetodoPago = idMetodoPago!!,
        nombre = nombre,
        descripcion = descripcion,
        estado = estado
    )
}

data class MetodoPagoResponse(
    val idMetodoPago: Int,
    val nombre: String,
    val descripcion: String?,
    val estado: Boolean
)
