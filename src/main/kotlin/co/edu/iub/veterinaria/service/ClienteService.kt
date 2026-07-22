package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.cliente.ClienteProfileRequest
import co.edu.iub.veterinaria.dto.cliente.ClienteRequest
import co.edu.iub.veterinaria.dto.cliente.ClienteResponse
import co.edu.iub.veterinaria.dto.cliente.CrearUsuarioClienteRequest
import co.edu.iub.veterinaria.exception.DuplicateResourceException
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.Cliente
import co.edu.iub.veterinaria.model.Usuario
import co.edu.iub.veterinaria.model.UsuarioRol
import co.edu.iub.veterinaria.repository.ClienteRepository
import co.edu.iub.veterinaria.repository.RolRepository
import co.edu.iub.veterinaria.repository.UsuarioRepository
import co.edu.iub.veterinaria.repository.UsuarioRolRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClienteService(
    private val clienteRepository: ClienteRepository,
    private val usuarioRepository: UsuarioRepository,
    private val usuarioRolRepository: UsuarioRolRepository,
    private val rolRepository: RolRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional(readOnly = true)
    fun listar(): List<ClienteResponse> {
        return clienteRepository.findAll().map { toResponse(it) }
    }

    @Transactional(readOnly = true)
    fun buscarPorId(id: Int): ClienteResponse {

        val cliente = clienteRepository.findById(id)
            .orElseThrow {
                ResourceNotFoundException("Cliente no encontrado")
            }

        return toResponse(cliente)
    }

    @Transactional
    fun crear(request: ClienteRequest): ClienteResponse {

        if (clienteRepository.existsByNumeroDocumento(request.numeroDocumento))
            throw DuplicateResourceException("Ya existe ese documento")

        request.correo?.let {
            if (clienteRepository.existsByCorreo(it))
                throw DuplicateResourceException("Ya existe ese correo")
        }

        val cliente = Cliente().apply {

            tipoDocumento = request.tipoDocumento
            numeroDocumento = request.numeroDocumento
            primerNombre = request.primerNombre
            segundoNombre = request.segundoNombre
            primerApellido = request.primerApellido
            segundoApellido = request.segundoApellido
            telefono = request.telefono
            correo = request.correo
            direccion = request.direccion
        }

        return toResponse(clienteRepository.save(cliente))
    }

    @Transactional
    fun actualizar(id: Int, request: ClienteRequest): ClienteResponse {

        val cliente = clienteRepository.findById(id)
            .orElseThrow {
                ResourceNotFoundException("Cliente no encontrado")
            }

        if (
            cliente.numeroDocumento != request.numeroDocumento &&
            clienteRepository.existsByNumeroDocumento(request.numeroDocumento)
        ) {
            throw DuplicateResourceException("Ya existe ese número de documento")
        }

        request.correo?.let { correo ->

            if (correo != cliente.correo &&
                clienteRepository.existsByCorreo(correo)
            ) {
                throw DuplicateResourceException("Ya existe ese correo")
            }
        }

        cliente.apply {
            tipoDocumento = request.tipoDocumento
            numeroDocumento = request.numeroDocumento
            primerNombre = request.primerNombre
            segundoNombre = request.segundoNombre
            primerApellido = request.primerApellido
            segundoApellido = request.segundoApellido
            telefono = request.telefono
            correo = request.correo
            direccion = request.direccion
        }

        return toResponse(clienteRepository.save(cliente))
    }

    @Transactional
    fun eliminar(id: Int) {

        val cliente = clienteRepository.findById(id)
            .orElseThrow {
                ResourceNotFoundException("Cliente no encontrado")
            }

        cliente.estado = false

        clienteRepository.save(cliente)
    }

    @Transactional(readOnly = true)
    fun buscarPerfilPorUsuarioId(idUsuario: Int): ClienteResponse {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado") }
        val cliente = usuario.cliente
            ?: throw ResourceNotFoundException("El usuario no está asociado a un cliente")
        return toResponse(cliente)
    }

    @Transactional
    fun actualizarPerfil(idUsuario: Int, request: ClienteProfileRequest): ClienteResponse {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado") }
        val cliente = usuario.cliente
            ?: throw ResourceNotFoundException("El usuario no está asociado a un cliente")

        request.correo?.let { correo ->
            if (correo != cliente.correo && clienteRepository.existsByCorreo(correo)) {
                throw DuplicateResourceException("Ya existe ese correo")
            }
        }

        cliente.apply {
            request.primerNombre?.let { primerNombre = it }
            request.segundoNombre?.let { segundoNombre = it }
            request.primerApellido?.let { primerApellido = it }
            request.segundoApellido?.let { segundoApellido = it }
            request.telefono?.let { telefono = it }
            request.correo?.let { correo = it }
            request.direccion?.let { direccion = it }
        }

        return toResponse(clienteRepository.save(cliente))
    }

    @Transactional
    fun crearUsuario(idCliente: Int, request: CrearUsuarioClienteRequest): Map<String, Any> {
        val cliente = clienteRepository.findById(idCliente)
            .orElseThrow { ResourceNotFoundException("Cliente no encontrado") }

        if (usuarioRepository.findByCorreo(request.email) != null) {
            throw DuplicateResourceException("El correo ya está registrado")
        }

        val nombreUsuario = request.nombreUsuario ?: request.email.substringBefore("@")
        if (usuarioRepository.existsByNombreUsuario(nombreUsuario)) {
            throw DuplicateResourceException("El nombre de usuario ya existe")
        }

        val usuario = Usuario().apply {
            this.cliente = cliente
            this.nombreUsuario = nombreUsuario
            passwordHash = passwordEncoder.encode(request.password)
        }
        usuarioRepository.save(usuario)

        val rolCliente = rolRepository.findByNombre("CLIENTE")
            ?: throw ResourceNotFoundException("Rol CLIENTE no encontrado")
        usuarioRolRepository.save(UsuarioRol().apply {
            this.usuario = usuario
            this.rol = rolCliente
        })

        return mapOf(
            "idUsuario" to usuario.idUsuario!!,
            "nombreUsuario" to usuario.nombreUsuario,
            "correo" to request.email,
            "mensaje" to "Usuario creado exitosamente para el cliente"
        )
    }

    private fun toResponse(cliente: Cliente): ClienteResponse {

        return ClienteResponse(

            idCliente = cliente.idCliente!!,
            tipoDocumento = cliente.tipoDocumento,
            numeroDocumento = cliente.numeroDocumento,
            primerNombre = cliente.primerNombre,
            segundoNombre = cliente.segundoNombre,
            primerApellido = cliente.primerApellido,
            segundoApellido = cliente.segundoApellido,
            telefono = cliente.telefono,
            correo = cliente.correo,
            direccion = cliente.direccion,
            estado = cliente.estado,
            createdAt = cliente.createdAt,
            updatedAt = cliente.updatedAt
        )
    } }