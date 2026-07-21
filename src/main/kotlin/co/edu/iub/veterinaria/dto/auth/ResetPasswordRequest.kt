package co.edu.iub.veterinaria.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class ResetPasswordRequest(
    @field:Email @field:NotBlank val correo: String
)
