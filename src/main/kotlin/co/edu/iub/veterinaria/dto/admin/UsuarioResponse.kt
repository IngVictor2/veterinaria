package co.edu.iub.veterinaria.dto.admin

import java.time.LocalDateTime

data class UsuarioResponse(
    val idUsuario: Int,
    val nombreUsuario: String,
    val correo: String?,
    val nombreCompleto: String,
    val tipoCuenta: String,
    val idCliente: Int?,
    val idEmpleado: Int?,
    val cargo: String?,
    val roles: List<String>,
    val estado: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)