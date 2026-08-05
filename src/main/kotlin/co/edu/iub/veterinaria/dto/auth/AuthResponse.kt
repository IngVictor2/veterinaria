package co.edu.iub.veterinaria.dto.auth

data class AuthResponse(
    val token: String,
    val tipo: String = "Bearer",
    val idUsuario: Int,
    val nombreUsuario: String,
    val correo: String,
    val roles: List<String>
)
