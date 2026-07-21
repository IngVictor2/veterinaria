package co.edu.iub.veterinaria.dto.admin

data class RolModuloResponse(
    val idRolModulo: Int,
    val idRol: Int,
    val idModulo: Int,
    val nombreModulo: String,
    val estado: Boolean
)
