package co.edu.iub.veterinaria.dto.admin

data class UsuarioRolResponse(
    val idUsuarioRol: Int,
    val idUsuario: Int,
    val idRol: Int,
    val nombreRol: String,
    val estado: Boolean
)
