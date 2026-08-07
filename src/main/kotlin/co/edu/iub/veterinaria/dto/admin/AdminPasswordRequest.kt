package co.edu.iub.veterinaria.dto.admin

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AdminPasswordRequest(
    @field:NotBlank @field:Size(min = 8) val nuevaPassword: String
)
