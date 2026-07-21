package co.edu.iub.veterinaria.dto.auth

import co.edu.iub.veterinaria.model.TipoDocumento
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotNull val tipoDocumento: TipoDocumento,
    @field:NotBlank val numeroDocumento: String,
    @field:NotBlank val primerNombre: String,
    val segundoNombre: String? = null,
    @field:NotBlank val primerApellido: String,
    val segundoApellido: String? = null,
    val telefono: String? = null,
    @field:Email @field:NotBlank val email: String,
    @field:NotBlank @field:Size(min = 8) val password: String
)
