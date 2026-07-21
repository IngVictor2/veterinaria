package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.servicio.ServicioRequest
import co.edu.iub.veterinaria.dto.servicio.ServicioResponse
import co.edu.iub.veterinaria.exception.DuplicateResourceException
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.Servicio
import co.edu.iub.veterinaria.model.TipoServicio
import co.edu.iub.veterinaria.repository.ServicioRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ServicioService(
    private val servicioRepository: ServicioRepository
) {
    @Transactional(readOnly = true)
    fun listar(): List<ServicioResponse> = servicioRepository.findAll().map { toResponse(it) }

    @Transactional(readOnly = true)
    fun listarPorTipo(tipo: TipoServicio): List<ServicioResponse> =
        servicioRepository.findByTipoServicio(tipo).map { toResponse(it) }

    @Transactional(readOnly = true)
    fun buscarPorId(id: Int): ServicioResponse {
        val servicio = servicioRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Servicio no encontrado") }
        return toResponse(servicio)
    }

    @Transactional
    fun crear(request: ServicioRequest): ServicioResponse {
        if (servicioRepository.existsByNombreIgnoreCase(request.nombre)) {
            throw DuplicateResourceException("Ya existe un servicio con el nombre '${request.nombre}'")
        }
        val servicio = Servicio().apply {
            nombre = request.nombre
            descripcion = request.descripcion
            tipoServicio = request.tipoServicio
            precio = request.precio
        }
        return toResponse(servicioRepository.save(servicio))
    }

    @Transactional
    fun actualizar(id: Int, request: ServicioRequest): ServicioResponse {
        val servicio = servicioRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Servicio no encontrado") }
        if (!servicio.nombre.equals(request.nombre, ignoreCase = true) &&
            servicioRepository.existsByNombreIgnoreCase(request.nombre)
        ) {
            throw DuplicateResourceException("Ya existe un servicio con el nombre '${request.nombre}'")
        }
        servicio.apply {
            nombre = request.nombre
            descripcion = request.descripcion
            tipoServicio = request.tipoServicio
            precio = request.precio
        }
        return toResponse(servicioRepository.save(servicio))
    }

    @Transactional
    fun eliminar(id: Int) {
        val servicio = servicioRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Servicio no encontrado") }
        servicio.estado = false
        servicioRepository.save(servicio)
    }

    private fun toResponse(s: Servicio) = ServicioResponse(
        idServicio = s.idServicio!!,
        nombre = s.nombre,
        descripcion = s.descripcion,
        tipoServicio = s.tipoServicio,
        precio = s.precio,
        estado = s.estado,
        createdAt = s.createdAt,
        updatedAt = s.updatedAt
    )
}
