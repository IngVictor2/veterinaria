package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.historial.HistorialResponse
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.repository.HistorialMascotaRepository
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/historial")
class HistorialController(
    private val historialMascotaRepository: HistorialMascotaRepository
) {
    @Transactional(readOnly = true)
    @GetMapping("/mascota/{idMascota}")
    fun listarPorMascota(@PathVariable idMascota: Int): List<HistorialResponse> =
        historialMascotaRepository.findByMascotaIdMascota(idMascota).map { h ->
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
