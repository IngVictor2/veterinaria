package co.edu.iub.veterinaria.dto.empleado

import co.edu.iub.veterinaria.model.TipoDocumento
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate

data class EmpleadoRequest(
    @field:NotNull val idCargo: Int,
    @field:NotNull val tipoDocumento: TipoDocumento,
    @field:NotBlank val numeroDocumento: String,
    @field:NotBlank val primerNombre: String,
    val segundoNombre: String? = null,
    @field:NotBlank val primerApellido: String,
    val segundoApellido: String? = null,
    val telefono: String? = null,
    val correo: String? = null,
    val direccion: String? = null,
    @field:NotNull val fechaIngreso: LocalDate,
    @field:NotBlank val nombreUsuario: String,
    @field:Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    val password: String? = null
)
