package co.edu.iub.veterinaria.dto.admin

import java.time.LocalDateTime

data class RolResponse(
    val idRol: Int,
    val nombre: String,
    val descripcion: String?,
    val estado: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
