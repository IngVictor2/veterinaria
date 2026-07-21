package co.edu.iub.veterinaria.dto.admin

import jakarta.validation.constraints.NotNull

data class AsignarPermisoRequest(
    @field:NotNull val idRol: Int,
    @field:NotNull val idModulo: Int
)
