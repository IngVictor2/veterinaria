package co.edu.iub.veterinaria.security

import co.edu.iub.veterinaria.repository.UsuarioRepository
import co.edu.iub.veterinaria.repository.UsuarioRolRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val usuarioRepository: UsuarioRepository,
    private val usuarioRolRepository: UsuarioRolRepository
) : UserDetailsService {

    override fun loadUserByUsername(correo: String): UserDetails {
        val usuario = usuarioRepository.findByCorreo(correo)
            ?: throw UsernameNotFoundException("Usuario no encontrado: $correo")

        val roles = usuarioRolRepository
            .findByUsuarioIdUsuarioAndEstadoTrue(usuario.idUsuario!!)
            .map { it.rol.nombre }

        return User.withUsername(correo)
            .password(usuario.passwordHash)
            .roles(*roles.toTypedArray())
            .disabled(!usuario.estado)
            .build()
    }
}
