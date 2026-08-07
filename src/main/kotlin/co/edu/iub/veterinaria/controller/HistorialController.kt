package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.historial.HistorialResponse
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.repository.HistorialMascotaRepository
import co.edu.iub.veterinaria.repository.MascotaRepository
import co.edu.iub.veterinaria.security.CurrentUserHelper
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/historial")
class HistorialController(
    private val historialMascotaRepository: HistorialMascotaRepository,
    private val mascotaRepository: MascotaRepository,
    private val currentUserHelper: CurrentUserHelper
) {
    @Transactional(readOnly = true)
    @GetMapping("/mascota/{idMascota}")
    fun listarPorMascota(
        @PathVariable idMascota: Int,
        authentication: Authentication
    ): List<HistorialResponse> {
        val clienteId = currentUserHelper.getClienteIdContextual(authentication)
        if (clienteId != null) {
            val mascota = mascotaRepository.findById(idMascota)
                .orElseThrow { ResourceNotFoundException("Mascota no encontrada") }
            if (mascota.cliente.idCliente != clienteId) {
                throw AccessDeniedException("No tiene permisos para ver el historial de esta mascota")
            }
        }
        return historialMascotaRepository.findByMascotaIdMascota(idMascota).map { h ->
            HistorialResponse(
                idHistorial = h.idHistorial ?: throw ResourceNotFoundException("Historial no encontrado"),
                idMascota = h.mascota.idMascota ?: throw ResourceNotFoundException("Mascota no encontrada"),
                nombreMascota = h.mascota.nombre,
                idCita = h.cita.idCita ?: throw ResourceNotFoundException("Cita no encontrada"),
                tipoHistorial = h.tipoHistorial,
                resumen = h.resumen,
                fechaRegistro = h.fechaRegistro,
                createdAt = h.createdAt,
                updatedAt = h.updatedAt
            )
        }
    }
}
