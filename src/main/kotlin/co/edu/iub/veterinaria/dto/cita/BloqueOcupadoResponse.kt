package co.edu.iub.veterinaria.dto.cita

import co.edu.iub.veterinaria.model.TipoServicio
import java.time.LocalTime

data class BloqueOcupadoResponse(
    val horaInicio: LocalTime,
    val duracionMinutos: Int,
    val tipoServicio: TipoServicio,
    val idEmpleado: Int,
    val nombreEmpleado: String
)
