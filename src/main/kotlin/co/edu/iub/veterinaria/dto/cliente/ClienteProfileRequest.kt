package co.edu.iub.veterinaria.dto.cliente

import jakarta.validation.constraints.Email

data class ClienteProfileRequest(
    val primerNombre: String? = null,
    val segundoNombre: String? = null,
    val primerApellido: String? = null,
    val segundoApellido: String? = null,
    val telefono: String? = null,
    @field:Email
    val correo: String? = null,
    val direccion: String? = null
)
