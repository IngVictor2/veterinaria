package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.cita.*
import co.edu.iub.veterinaria.exception.DuplicateResourceException
import co.edu.iub.veterinaria.exception.InvalidRequestException
import co.edu.iub.veterinaria.exception.InvalidStatusTransitionException
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.*
import co.edu.iub.veterinaria.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CitaService(
    private val citaRepository: CitaRepository,
    private val mascotaRepository: MascotaRepository,
    private val empleadoRepository: EmpleadoRepository,
    private val servicioRepository: ServicioRepository,
    private val consultaMedicaRepository: ConsultaMedicaRepository,
    private val servicioEsteticaRepository: ServicioEsteticaRepository,
    private val historialMascotaRepository: HistorialMascotaRepository
) {

    @Transactional(readOnly = true)
    fun listar(): List<CitaResponse> = citaRepository.findAll().map { toResponse(it) }

    @Transactional(readOnly = true)
    fun buscarPorId(id: Int): CitaResponse {
        val cita = citaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Cita no encontrada") }
        return toResponse(cita)
    }

    @Transactional(readOnly = true)
    fun listarPorCliente(idCliente: Int): List<CitaResponse> =
        citaRepository.findByMascotaClienteIdCliente(idCliente).map { toResponse(it) }

    @Transactional(readOnly = true)
    fun listarPorMascota(idMascota: Int): List<CitaResponse> =
        citaRepository.findByMascotaIdMascota(idMascota).map { toResponse(it) }

    @Transactional(readOnly = true)
    fun listarPorEmpleado(idEmpleado: Int): List<CitaResponse> =
        citaRepository.findByEmpleadoIdEmpleado(idEmpleado).map { toResponse(it) }

    private val transicionesValidas = mapOf(
        EstadoCita.PENDIENTE to setOf(EstadoCita.CONFIRMADA, EstadoCita.CANCELADA),
        EstadoCita.CONFIRMADA to setOf(EstadoCita.ATENDIDA, EstadoCita.CANCELADA),
        EstadoCita.ATENDIDA to emptySet(),
        EstadoCita.CANCELADA to emptySet()
    )

    @Transactional
    fun crear(request: CitaRequest): CitaResponse {
        val mascota = mascotaRepository.findById(request.idMascota)
            .orElseThrow { ResourceNotFoundException("Mascota no encontrada") }
        val empleado = empleadoRepository.findById(request.idEmpleado)
            .orElseThrow { ResourceNotFoundException("Empleado no encontrado") }
        val servicio = servicioRepository.findById(request.idServicio)
            .orElseThrow { ResourceNotFoundException("Servicio no encontrado") }

        val conflicto = citaRepository.findByFechaCita(request.fechaCita)
            .any { it.empleado.idEmpleado == request.idEmpleado &&
                    it.horaCita == request.horaCita &&
                    it.estadoCita != EstadoCita.CANCELADA }
        if (conflicto) {
            throw DuplicateResourceException("El empleado ya tiene una cita en esa fecha y hora")
        }

        val cita = Cita().apply {
            this.mascota = mascota
            this.empleado = empleado
            this.servicio = servicio
            fechaCita = request.fechaCita
            horaCita = request.horaCita
            motivo = request.motivo
            observaciones = request.observaciones
        }
        return toResponse(citaRepository.save(cita))
    }

    @Transactional
    fun reprogramar(id: Int, request: CitaRequest): CitaResponse {
        val cita = citaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Cita no encontrada") }
        if (cita.estadoCita == EstadoCita.ATENDIDA || cita.estadoCita == EstadoCita.CANCELADA) {
            throw InvalidRequestException("No se puede reprogramar una cita ${cita.estadoCita}")
        }
        val conflictos = citaRepository.findByFechaCita(request.fechaCita)
            .any { it.idCita != id &&
                    it.empleado.idEmpleado == request.idEmpleado &&
                    it.horaCita == request.horaCita &&
                    it.estadoCita != EstadoCita.CANCELADA }
        if (conflictos) {
            throw DuplicateResourceException("El empleado ya tiene una cita en esa fecha y hora")
        }
        cita.fechaCita = request.fechaCita
        cita.horaCita = request.horaCita
        cita.motivo = request.motivo
        cita.observaciones = request.observaciones
        return toResponse(citaRepository.save(cita))
    }

    @Transactional
    fun cambiarEstado(id: Int, nuevoEstado: EstadoCita) {
        val cita = citaRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Cita no encontrada") }
        val permitidos = transicionesValidas[cita.estadoCita]
            ?: throw InvalidStatusTransitionException("Estado actual inválido: ${cita.estadoCita}")
            if (nuevoEstado !in permitidos) {
            throw InvalidStatusTransitionException(
                "No se puede cambiar de ${cita.estadoCita} a $nuevoEstado. " +
                "Transiciones permitidas: ${permitidos.joinToString(", ")}"
            )
        }
        cita.estadoCita = nuevoEstado
        citaRepository.save(cita)
    }

    @Transactional
    fun cancelar(id: Int) {
        cambiarEstado(id, EstadoCita.CANCELADA)
    }

    @Transactional
    fun registrarConsulta(request: ConsultaRequest) {
        cambiarEstado(request.idCita, EstadoCita.ATENDIDA)
        val cita = citaRepository.findById(request.idCita)
            .orElseThrow { ResourceNotFoundException("Cita no encontrada") }

        if (consultaMedicaRepository.findByCitaIdCita(request.idCita) != null) {
            throw DuplicateResourceException("Esta cita ya tiene una consulta médica registrada")
        }

        val consulta = ConsultaMedica().apply {
            this.cita = cita
            peso = request.peso
            temperatura = request.temperatura
            sintomas = request.sintomas
            diagnosticoGeneral = request.diagnosticoGeneral
            tratamientoIndicado = request.tratamientoIndicado
            observaciones = request.observaciones
        }
        consultaMedicaRepository.save(consulta)

        val historial = HistorialMascota().apply {
            mascota = cita.mascota
            this.cita = cita
            this.consulta = consulta
            tipoHistorial = TipoHistorial.MEDICO
            resumen = "Consulta médica: ${request.sintomas.take(200)}"
        }
        historialMascotaRepository.save(historial)
    }

    @Transactional
    fun registrarEstetica(request: EsteticaRequest) {
        cambiarEstado(request.idCita, EstadoCita.ATENDIDA)
        val cita = citaRepository.findById(request.idCita)
            .orElseThrow { ResourceNotFoundException("Cita no encontrada") }

        if (servicioEsteticaRepository.findByCitaIdCita(request.idCita) != null) {
            throw DuplicateResourceException("Esta cita ya tiene un servicio estético registrado")
        }

        val estetica = ServicioEstetica().apply {
            this.cita = cita
            detalles = request.detalles
            observaciones = request.observaciones
        }
        servicioEsteticaRepository.save(estetica)

        val historial = HistorialMascota().apply {
            mascota = cita.mascota
            this.cita = cita
            this.servicioEstetica = estetica
            tipoHistorial = TipoHistorial.ESTETICA
            resumen = "Servicio estético: ${request.detalles?.take(200) ?: "Sin detalles"}"
        }
        historialMascotaRepository.save(historial)
    }

    private fun toResponse(c: Cita) = CitaResponse(
        idCita = c.idCita!!,
        idMascota = c.mascota.idMascota!!,
        nombreMascota = c.mascota.nombre,
        idCliente = c.mascota.cliente.idCliente!!,
        nombreCliente = "${c.mascota.cliente.primerNombre} ${c.mascota.cliente.primerApellido}",
        idEmpleado = c.empleado.idEmpleado!!,
        nombreEmpleado = "${c.empleado.primerNombre} ${c.empleado.primerApellido}",
        idServicio = c.servicio.idServicio!!,
        nombreServicio = c.servicio.nombre,
        fechaCita = c.fechaCita,
        horaCita = c.horaCita,
        motivo = c.motivo,
        estadoCita = c.estadoCita,
        observaciones = c.observaciones,
        createdAt = c.createdAt,
        updatedAt = c.updatedAt
    )
}
