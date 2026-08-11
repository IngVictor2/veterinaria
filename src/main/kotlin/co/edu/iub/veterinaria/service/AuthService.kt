package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.auth.*
import co.edu.iub.veterinaria.exception.DuplicateResourceException
import co.edu.iub.veterinaria.exception.InvalidCredentialsException
import co.edu.iub.veterinaria.exception.InvalidRequestException
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.model.*
import co.edu.iub.veterinaria.repository.*
import co.edu.iub.veterinaria.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
class AuthService(
    private val clienteRepository: ClienteRepository,
    private val empleadoRepository: EmpleadoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val usuarioRolRepository: UsuarioRolRepository,
    private val rolRepository: RolRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val rolModuloRepository: RolModuloRepository,
    private val mascotaRepository: MascotaRepository
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (clienteRepository.existsByCorreo(request.email)) {
            throw DuplicateResourceException("El correo ya está registrado")
        }
        if (clienteRepository.existsByNumeroDocumento(request.numeroDocumento)) {
            throw DuplicateResourceException("El número de documento ya está registrado")
        }

        val cliente = Cliente().apply {
            tipoDocumento = request.tipoDocumento
            numeroDocumento = request.numeroDocumento
            primerNombre = request.primerNombre
            segundoNombre = request.segundoNombre
            primerApellido = request.primerApellido
            segundoApellido = request.segundoApellido
            telefono = request.telefono
            direccion = request.direccion
            correo = request.email
        }
        clienteRepository.save(cliente)

        val usuario = Usuario().apply {
            this.cliente = cliente
            nombreUsuario = request.email.substringBefore("@")
            passwordHash = passwordEncoder.encode(request.password)
        }
        usuarioRepository.save(usuario)

        val rolCliente = rolRepository.findByNombre("CLIENTE")
            ?: throw ResourceNotFoundException("Rol CLIENTE no encontrado en BD")
        val usuarioRol = UsuarioRol().apply {
            this.usuario = usuario
            this.rol = rolCliente
        }
        usuarioRolRepository.save(usuarioRol)

        val roles = listOf("CLIENTE")
        val modulos = rolModuloRepository.findByRolIdRolAndEstadoTrue(rolCliente.idRol!!)
            .map { it.modulo.nombre }
            .distinct()
        val token = jwtTokenProvider.generateToken(usuario.idUsuario!!, usuario.nombreUsuario, roles)

        return AuthResponse(
            token = token,
            idUsuario = usuario.idUsuario!!,
            nombreUsuario = usuario.nombreUsuario,
            correo = cliente.correo!!,
            roles = roles,
            modulos = modulos
        )
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val usuario = usuarioRepository.findByCorreo(request.correo)
            ?: throw InvalidCredentialsException("Credenciales inválidas")

        if (!passwordEncoder.matches(request.password, usuario.passwordHash)) {
            throw InvalidCredentialsException("Credenciales inválidas")
        }
        if (!usuario.estado) {
            throw InvalidCredentialsException("Credenciales inválidas")
        }

        val usuarioRolesActivos = usuarioRolRepository
            .findByUsuarioIdUsuarioAndEstadoTrue(usuario.idUsuario!!)

        val rolesActivos = usuarioRolesActivos.map { it.rol.nombre }
        val modulos = usuarioRolesActivos
            .flatMap { rolModuloRepository.findByRolIdRolAndEstadoTrue(it.rol.idRol!!) }
            .map { it.modulo.nombre }
            .distinct()

        val correo = usuarioRepository.findCorreoByIdUsuario(usuario.idUsuario!!)
            ?: ""

        val token = jwtTokenProvider.generateToken(usuario.idUsuario!!, usuario.nombreUsuario, rolesActivos)

        return AuthResponse(
            token = token,
            idUsuario = usuario.idUsuario!!,
            nombreUsuario = usuario.nombreUsuario,
            correo = correo,
            roles = rolesActivos,
            modulos = modulos
        )
    }

    @Transactional
    fun changePassword(idUsuario: Int, request: ChangePasswordRequest) {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado") }

        if (!passwordEncoder.matches(request.passwordActual, usuario.passwordHash)) {
            throw InvalidRequestException("La contraseña actual es incorrecta")
        }

        usuario.passwordHash = passwordEncoder.encode(request.nuevaPassword)
        usuarioRepository.save(usuario)
    }

    @Transactional
    fun cambiarPasswordAdmin(idUsuario: Int, nuevaPassword: String) {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado") }
        usuario.passwordHash = passwordEncoder.encode(nuevaPassword)
        usuarioRepository.save(usuario)
    }

    @Transactional
    fun cambiarEstadoCuenta(idUsuario: Int, estado: Boolean, idAdminActual: Int, esAdmin: Boolean) {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado") }
        if (!esAdmin && usuario.empleado != null) {
            throw AccessDeniedException("No tiene permisos para cambiar el estado de cuentas de empleados")
        }
        if (!estado && idUsuario == idAdminActual) {
            throw InvalidRequestException("No puedes desactivar tu propia cuenta")
        }
        aplicarEstado(usuario, estado)
    }

    @Transactional
    fun cambiarEstadoPorCliente(idCliente: Int, estado: Boolean, idAdminActual: Int) {
        val cliente = clienteRepository.findById(idCliente)
            .orElseThrow { ResourceNotFoundException("Cliente no encontrado") }
        val usuario = usuarioRepository.findByClienteIdCliente(idCliente)
        if (!estado && usuario?.idUsuario == idAdminActual) {
            throw InvalidRequestException("No puedes desactivar tu propia cuenta")
        }
        if (usuario != null) {
            aplicarEstado(usuario, estado)
        } else {
            cliente.estado = estado
            mascotaRepository.findByClienteIdCliente(idCliente).forEach { it.estado = estado }
            clienteRepository.save(cliente)
        }
    }

    @Transactional
    fun cambiarEstadoPorEmpleado(idEmpleado: Int, estado: Boolean, idAdminActual: Int) {
        val empleado = empleadoRepository.findById(idEmpleado)
            .orElseThrow { ResourceNotFoundException("Empleado no encontrado") }
        val usuario = usuarioRepository.findByEmpleadoIdEmpleado(idEmpleado)
        if (usuario != null) {
            if (!estado && usuario.idUsuario == idAdminActual) {
                throw InvalidRequestException("No puedes desactivar tu propia cuenta")
            }
            aplicarEstado(usuario, estado)
        } else {
            empleado.estado = estado
            empleadoRepository.save(empleado)
        }
    }

    private fun aplicarEstado(usuario: Usuario, estado: Boolean) {
        usuario.estado = estado
        usuario.cliente?.let { c ->
            c.estado = estado
            mascotaRepository.findByClienteIdCliente(c.idCliente!!).forEach { it.estado = estado }
        }
        usuario.empleado?.let { it.estado = estado }
        usuarioRepository.save(usuario)
    }

    @Transactional
    fun requestPasswordReset(request: ResetPasswordRequest): String? {
        val usuario = usuarioRepository.findByCorreo(request.correo)
            ?: return null

        val token = PasswordResetToken().apply {
            this.usuario = usuario
            this.token = UUID.randomUUID().toString()
            this.fechaExpiracion = LocalDateTime.now().plusHours(1)
        }
        passwordResetTokenRepository.save(token)

        return token.token
    }

    @Transactional
    fun resetPassword(tokenStr: String, nuevaPassword: String) {
        val token = passwordResetTokenRepository.findByToken(tokenStr)
            ?: throw ResourceNotFoundException("Token inválido")

        if (token.usado || token.fechaExpiracion.isBefore(LocalDateTime.now())) {
            throw InvalidRequestException("Token expirado o ya usado")
        }

        val usuario = token.usuario
        usuario.passwordHash = passwordEncoder.encode(nuevaPassword)
        usuarioRepository.save(usuario)

        token.usado = true
        passwordResetTokenRepository.save(token)
    }
}
