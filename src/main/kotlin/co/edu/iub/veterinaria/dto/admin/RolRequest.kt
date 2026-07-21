package co.edu.iub.veterinaria.dto.admin

import jakarta.validation.constraints.NotBlank

data class RolRequest(
    @field:NotBlank val nombre: String,
    val descripcion: String? = null
)
