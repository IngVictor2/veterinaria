package co.edu.iub.veterinaria.dto.cliente

import co.edu.iub.veterinaria.model.TipoDocumento
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ClienteRequest(

    @field:NotNull(message = "El tipo de documento es obligatorio")
    val tipoDocumento: TipoDocumento,

    @field:NotBlank(message = "El número de documento es obligatorio")
    val numeroDocumento: String,

    @field:NotBlank(message = "El primer nombre es obligatorio")
    val primerNombre: String,

    val segundoNombre: String? = null,

    @field:NotBlank(message = "El primer apellido es obligatorio")
    val primerApellido: String,

    val segundoApellido: String? = null,

    val telefono: String? = null,

    @field:Email(message = "Correo inválido")
    val correo: String? = null,

    val direccion: String? = null
)
