package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.mascota.MascotaRequest
import co.edu.iub.veterinaria.dto.mascota.MascotaResponse
import co.edu.iub.veterinaria.exception.InvalidRequestException
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.Mascota
import co.edu.iub.veterinaria.repository.ClienteRepository
import co.edu.iub.veterinaria.repository.MascotaRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MascotaService(
    private val mascotaRepository: MascotaRepository,
    private val clienteRepository: ClienteRepository
) {

    @Transactional(readOnly = true)
    fun listar(): List<MascotaResponse> {
        return mascotaRepository.findAll().map { toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun buscarPorId(id: Int, idCliente: Int? = null): MascotaResponse {
        val mascota = mascotaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Mascota no encontrada") }
        if (idCliente != null && mascota.cliente.idCliente != idCliente) {
            throw AccessDeniedException("No tiene permisos para ver esta mascota")
        }
        return toResponse(mascota)
    }

    @Transactional(readOnly = true)
    fun listarPorCliente(idCliente: Int): List<MascotaResponse> {
        return mascotaRepository.findByClienteIdClienteAndEstadoTrue(idCliente).map { toResponse(it) }
    }

    @Transactional
    fun crear(request: MascotaRequest, idClienteContextual: Int? = null): MascotaResponse {
        if (idClienteContextual != null && request.idCliente != idClienteContextual) {
            throw AccessDeniedException("No tiene permisos para crear mascotas para otro cliente")
        }
        if (idClienteContextual != null &&
            mascotaRepository.countByClienteIdClienteAndEstadoTrue(request.idCliente) >= LIMITE_MASCOTAS
        ) {
            throw InvalidRequestException(
                "Límite de $LIMITE_MASCOTAS mascotas alcanzado; solicita al recepcionista agregar otra"
            )
        }

        val cliente = clienteRepository.findById(request.idCliente)
            .orElseThrow { ResourceNotFoundException("Cliente no encontrado") }

        val mascota = Mascota().apply {
            this.cliente = cliente
            nombre = request.nombre
            especie = request.especie
            raza = request.raza
            sexo = request.sexo
            fechaNacimiento = request.fechaNacimiento
            peso = request.peso
            observaciones = request.observaciones
        }
        return toResponse(mascotaRepository.save(mascota))
    }

    @Transactional
    fun actualizar(id: Int, request: MascotaRequest, idClienteContextual: Int? = null): MascotaResponse {
        val mascota = mascotaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Mascota no encontrada") }

        if (idClienteContextual != null && mascota.cliente.idCliente != idClienteContextual) {
            throw AccessDeniedException("No tiene permisos para editar esta mascota")
        }
        if (idClienteContextual != null && request.idCliente != idClienteContextual) {
            throw AccessDeniedException("No tiene permisos para cambiar la mascota a otro cliente")
        }

        val cliente = clienteRepository.findById(request.idCliente)
            .orElseThrow { ResourceNotFoundException("Cliente no encontrado") }
        mascota.cliente = cliente

        request.nombre.let { mascota.nombre = it }
        request.especie.let { mascota.especie = it }
        request.raza.let { mascota.raza = it }
        request.sexo.let { mascota.sexo = it }
        request.fechaNacimiento.let { mascota.fechaNacimiento = it }
        request.peso.let { mascota.peso = it }
        request.observaciones.let { mascota.observaciones = it }

        return toResponse(mascotaRepository.save(mascota))
    }

    @Transactional
    fun eliminar(id: Int) {
        val mascota = mascotaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Mascota no encontrada") }
        mascota.estado = false
        mascotaRepository.save(mascota)
    }

    private fun toResponse(m: Mascota) = MascotaResponse(
        idMascota = m.idMascota!!,
        idCliente = m.cliente.idCliente!!,
        nombreCliente = "${m.cliente.primerNombre} ${m.cliente.primerApellido}",
        nombre = m.nombre,
        especie = m.especie,
        raza = m.raza,
        sexo = m.sexo,
        fechaNacimiento = m.fechaNacimiento,
        peso = m.peso,
        observaciones = m.observaciones,
        estado = m.estado,
        createdAt = m.createdAt,
        updatedAt = m.updatedAt
    )

    companion object {
        const val LIMITE_MASCOTAS = 5L
    }
}
