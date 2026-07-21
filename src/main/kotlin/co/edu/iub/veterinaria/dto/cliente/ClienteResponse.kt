package co.edu.iub.veterinaria.dto.cliente

import co.edu.iub.veterinaria.model.TipoDocumento
import java.time.LocalDateTime

data class ClienteResponse(

    val idCliente: Int,

    val tipoDocumento: TipoDocumento,

    val numeroDocumento: String,

    val primerNombre: String,

    val segundoNombre: String?,

    val primerApellido: String,

    val segundoApellido: String?,

    val telefono: String?,

    val correo: String?,

    val direccion: String?,

    val estado: Boolean,

    val createdAt: LocalDateTime?,

    val updatedAt: LocalDateTime?
)
