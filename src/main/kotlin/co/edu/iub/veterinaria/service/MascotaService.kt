package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.mascota.MascotaRequest
import co.edu.iub.veterinaria.dto.mascota.MascotaResponse
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.Mascota
import co.edu.iub.veterinaria.repository.ClienteRepository
import co.edu.iub.veterinaria.repository.MascotaRepository
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
    fun buscarPorId(id: Int): MascotaResponse {
        val mascota = mascotaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Mascota no encontrada") }
        return toResponse(mascota)
    }

    @Transactional(readOnly = true)
    fun listarPorCliente(idCliente: Int): List<MascotaResponse> {
        return mascotaRepository.findByClienteIdClienteAndEstadoTrue(idCliente).map { toResponse(it) }
    }

    @Transactional
    fun crear(request: MascotaRequest): MascotaResponse {
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
    fun actualizar(id: Int, request: MascotaRequest): MascotaResponse {
        val mascota = mascotaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Mascota no encontrada") }

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
}
