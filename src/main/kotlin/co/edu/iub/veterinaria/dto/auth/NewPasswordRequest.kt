package co.edu.iub.veterinaria.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class NewPasswordRequest(
    @field:NotBlank @field:Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    val password: String
)
