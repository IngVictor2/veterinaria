package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.empleado.EmpleadoRequest
import co.edu.iub.veterinaria.dto.empleado.EmpleadoResponse
import co.edu.iub.veterinaria.exception.DuplicateResourceException
import co.edu.iub.veterinaria.exception.InvalidRequestException
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.*
import co.edu.iub.veterinaria.repository.*
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EmpleadoService(
    private val empleadoRepository: EmpleadoRepository,
    private val cargoRepository: CargoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val usuarioRolRepository: UsuarioRolRepository,
    private val rolRepository: RolRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional(readOnly = true)
    fun listar(): List<EmpleadoResponse> {
        return empleadoRepository.findAll().map { toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun buscarPorId(id: Int): EmpleadoResponse {
        val empleado = empleadoRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Empleado no encontrado") }
        return toResponse(empleado)
    }

    @Transactional
    fun crear(request: EmpleadoRequest): EmpleadoResponse {
        if (empleadoRepository.existsByNumeroDocumento(request.numeroDocumento))
            throw DuplicateResourceException("Ya existe ese documento")
        request.correo?.let {
            if (empleadoRepository.existsByCorreo(it))
                throw DuplicateResourceException("Ya existe ese correo")
        }
        if (usuarioRepository.existsByNombreUsuario(request.nombreUsuario))
            throw DuplicateResourceException("El nombre de usuario ya existe")

        val cargo = cargoRepository.findById(request.idCargo)
            .orElseThrow { ResourceNotFoundException("Cargo no encontrado") }

        val empleado = Empleado().apply {
            this.cargo = cargo
            tipoDocumento = request.tipoDocumento
            numeroDocumento = request.numeroDocumento
            primerNombre = request.primerNombre
            segundoNombre = request.segundoNombre
            primerApellido = request.primerApellido
            segundoApellido = request.segundoApellido
            telefono = request.telefono
            correo = request.correo
            direccion = request.direccion
            fechaIngreso = request.fechaIngreso
        }
        empleadoRepository.save(empleado)

        val usuario = Usuario().apply {
            this.empleado = empleado
            nombreUsuario = request.nombreUsuario
            passwordHash = passwordEncoder.encode(request.password ?: throw InvalidRequestException("La contraseña es obligatoria"))
        }
        usuarioRepository.save(usuario)

        return toResponse(empleado)
    }

    @Transactional
    fun actualizar(id: Int, request: EmpleadoRequest): EmpleadoResponse {
        val empleado = empleadoRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Empleado no encontrado") }

        if (empleado.numeroDocumento != request.numeroDocumento &&
            empleadoRepository.existsByNumeroDocumento(request.numeroDocumento)
        ) throw DuplicateResourceException("Ya existe ese documento")

        request.correo?.let {
            if (it != empleado.correo && empleadoRepository.existsByCorreo(it))
                throw DuplicateResourceException("Ya existe ese correo")
        }

        val cargo = cargoRepository.findById(request.idCargo)
            .orElseThrow { ResourceNotFoundException("Cargo no encontrado") }

        empleado.apply {
            this.cargo = cargo
            tipoDocumento = request.tipoDocumento
            numeroDocumento = request.numeroDocumento
            primerNombre = request.primerNombre
            segundoNombre = request.segundoNombre
            primerApellido = request.primerApellido
            segundoApellido = request.segundoApellido
            telefono = request.telefono
            correo = request.correo
            direccion = request.direccion
            fechaIngreso = request.fechaIngreso
        }
        empleadoRepository.save(empleado)

        val usuario = usuarioRepository.findByEmpleadoIdEmpleado(id)
        if (usuario != null) {
            if (request.nombreUsuario != usuario.nombreUsuario) {
                if (usuarioRepository.existsByNombreUsuario(request.nombreUsuario)) {
                    throw DuplicateResourceException("El nombre de usuario '${request.nombreUsuario}' ya existe")
                }
                usuario.nombreUsuario = request.nombreUsuario
            }
            request.password?.let {
                usuario.passwordHash = passwordEncoder.encode(it)
            }
            usuarioRepository.save(usuario)
        }

        return toResponse(empleado)
    }

    @Transactional
    fun eliminar(id: Int) {
        val empleado = empleadoRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Empleado no encontrado") }
        empleado.estado = false
        empleadoRepository.save(empleado)
    }

    private fun toResponse(e: Empleado): EmpleadoResponse {
        val usuario = usuarioRepository.findByEmpleadoIdEmpleado(e.idEmpleado!!)
        val roles = usuario?.let { u ->
            usuarioRolRepository.findByUsuarioIdUsuarioAndEstadoTrue(u.idUsuario!!)
                .map { it.rol.nombre }
        } ?: emptyList()

        return EmpleadoResponse(
            idEmpleado = e.idEmpleado!!,
            idCargo = e.cargo.idCargo!!,
            nombreCargo = e.cargo.nombre,
            tipoDocumento = e.tipoDocumento,
            numeroDocumento = e.numeroDocumento,
            primerNombre = e.primerNombre,
            segundoNombre = e.segundoNombre,
            primerApellido = e.primerApellido,
            segundoApellido = e.segundoApellido,
            telefono = e.telefono,
            correo = e.correo,
            direccion = e.direccion,
            fechaIngreso = e.fechaIngreso,
            roles = roles,
            estado = e.estado,
            createdAt = e.createdAt,
            updatedAt = e.updatedAt
        )
    }
}
