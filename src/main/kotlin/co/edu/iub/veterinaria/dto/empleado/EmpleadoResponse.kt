package co.edu.iub.veterinaria.dto.empleado

import co.edu.iub.veterinaria.model.TipoDocumento
import java.time.LocalDate
import java.time.LocalDateTime

data class EmpleadoResponse(
    val idEmpleado: Int,
    val idCargo: Int,
    val nombreCargo: String,
    val tipoDocumento: TipoDocumento,
    val numeroDocumento: String,
    val primerNombre: String,
    val segundoNombre: String?,
    val primerApellido: String,
    val segundoApellido: String?,
    val telefono: String?,
    val correo: String?,
    val direccion: String?,
    val fechaIngreso: LocalDate,
    val roles: List<String>,
    val estado: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
)
