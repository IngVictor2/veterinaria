package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.calificacion.CalificacionRequest
import co.edu.iub.veterinaria.dto.calificacion.CalificacionResponse
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.Calificacion
import co.edu.iub.veterinaria.model.EstadoCita
import co.edu.iub.veterinaria.repository.CalificacionRepository
import co.edu.iub.veterinaria.repository.CitaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CalificacionService(
    private val calificacionRepository: CalificacionRepository,
    private val citaRepository: CitaRepository
) {
    @Transactional(readOnly = true)
    fun listarPorCliente(idCliente: Int): List<CalificacionResponse> =
        calificacionRepository.findByCitaMascotaClienteIdCliente(idCliente).map { toResponse(it) }

    @Transactional
    fun registrar(request: CalificacionRequest): CalificacionResponse {
        val cita = citaRepository.findById(request.idCita)
            .orElseThrow { ResourceNotFoundException("Cita no encontrada") }

        if (cita.estadoCita != EstadoCita.ATENDIDA) {
            throw IllegalArgumentException("Solo se pueden calificar citas atendidas")
        }

        if (calificacionRepository.findByCitaIdCita(request.idCita) != null) {
            throw IllegalArgumentException("Esta cita ya fue calificada")
        }

        val calificacion = Calificacion().apply {
            this.cita = cita
            puntuacion = request.puntuacion
            comentario = request.comentario
        }
        return toResponse(calificacionRepository.save(calificacion))
    }

    private fun toResponse(c: Calificacion) = CalificacionResponse(
        idCalificacion = c.idCalificacion!!,
        idCita = c.cita.idCita!!,
        idCliente = c.cita.mascota.cliente.idCliente!!,
        puntuacion = c.puntuacion,
        comentario = c.comentario,
        createdAt = c.createdAt,
        updatedAt = c.updatedAt
    )
}
