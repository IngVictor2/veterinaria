package co.edu.iub.veterinaria.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class ChangePasswordRequest(
    @field:NotBlank val passwordActual: String,
    @field:NotBlank @field:Size(min = 8) val nuevaPassword: String
)
