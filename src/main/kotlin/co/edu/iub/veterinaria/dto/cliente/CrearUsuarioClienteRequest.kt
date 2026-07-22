package co.edu.iub.veterinaria.dto.cliente

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CrearUsuarioClienteRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Size(min = 8)
    val password: String,

    val nombreUsuario: String? = null
)
