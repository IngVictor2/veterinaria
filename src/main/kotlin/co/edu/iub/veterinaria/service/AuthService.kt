package co.edu.iub.veterinaria.service

import co.edu.iub.veterinaria.dto.auth.*
import co.edu.iub.veterinaria.exception.DuplicateResourceException
import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import co.edu.iub.veterinaria.model.*
import co.edu.iub.veterinaria.repository.*
import co.edu.iub.veterinaria.security.JwtTokenProvider
import org.springframework.security.crypto.password.PasswordEncoder
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
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (clienteRepository.existsByCorreo(request.email)) {
            throw DuplicateResourceException("El correo ya está registrado")
        }

        val cliente = Cliente().apply {
            tipoDocumento = request.tipoDocumento
            numeroDocumento = request.numeroDocumento
            primerNombre = request.primerNombre
            segundoNombre = request.segundoNombre
            primerApellido = request.primerApellido
            segundoApellido = request.segundoApellido
            telefono = request.telefono
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
        val token = jwtTokenProvider.generateToken(usuario.idUsuario!!, usuario.nombreUsuario, roles)

        return AuthResponse(
            token = token,
            idUsuario = usuario.idUsuario!!,
            nombreUsuario = usuario.nombreUsuario,
            correo = cliente.correo!!,
            roles = roles
        )
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val usuario = usuarioRepository.findByCorreo(request.correo)
            ?: throw BadCredentialsException("Credenciales inválidas")

        if (!passwordEncoder.matches(request.password, usuario.passwordHash)) {
            throw BadCredentialsException("Credenciales inválidas")
        }
        if (!usuario.estado) {
            throw BadCredentialsException("Credenciales inválidas")
        }

        val rolesActivos = usuarioRolRepository
            .findByUsuarioIdUsuarioAndEstadoTrue(usuario.idUsuario!!)
            .map { it.rol.nombre }

        val correo = usuarioRepository.findCorreoByIdUsuario(usuario.idUsuario!!)
            ?: ""

        val token = jwtTokenProvider.generateToken(usuario.idUsuario!!, usuario.nombreUsuario, rolesActivos)

        return AuthResponse(
            token = token,
            idUsuario = usuario.idUsuario!!,
            nombreUsuario = usuario.nombreUsuario,
            correo = correo,
            roles = rolesActivos
        )
    }

    @Transactional
    fun changePassword(idUsuario: Int, request: ChangePasswordRequest) {
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado") }

        if (!passwordEncoder.matches(request.passwordActual, usuario.passwordHash)) {
            throw IllegalArgumentException("La contraseña actual es incorrecta")
        }

        usuario.passwordHash = passwordEncoder.encode(request.nuevaPassword)
        usuarioRepository.save(usuario)
    }

    @Transactional
    fun requestPasswordReset(request: ResetPasswordRequest) {
        val usuario = usuarioRepository.findByCorreo(request.correo)
            ?: return

        val token = PasswordResetToken().apply {
            this.usuario = usuario
            this.token = UUID.randomUUID().toString()
            this.fechaExpiracion = LocalDateTime.now().plusHours(1)
        }
        passwordResetTokenRepository.save(token)
    }

    @Transactional
    fun resetPassword(tokenStr: String, nuevaPassword: String) {
        val token = passwordResetTokenRepository.findByToken(tokenStr)
            ?: throw ResourceNotFoundException("Token inválido")

        if (token.usado || token.fechaExpiracion.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Token expirado o ya usado")
        }

        val usuario = token.usuario
        usuario.passwordHash = passwordEncoder.encode(nuevaPassword)
        usuarioRepository.save(usuario)

        token.usado = true
        passwordResetTokenRepository.save(token)
    }
}
