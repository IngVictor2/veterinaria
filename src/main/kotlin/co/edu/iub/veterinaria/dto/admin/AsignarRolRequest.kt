package co.edu.iub.veterinaria.dto.admin

import jakarta.validation.constraints.NotNull

data class AsignarRolRequest(
    @field:NotNull val idUsuario: Int,
    @field:NotNull val idRol: Int
)
