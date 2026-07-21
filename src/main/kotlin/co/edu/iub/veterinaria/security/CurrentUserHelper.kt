package co.edu.iub.veterinaria.security

import co.edu.iub.veterinaria.exception.ResourceNotFoundException
import co.edu.iub.veterinaria.repository.UsuarioRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class CurrentUserHelper(
    private val usuarioRepository: UsuarioRepository
) {
    fun getClienteId(authentication: Authentication): Int {
        val idUsuario = authentication.principal as Int
        val usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow { ResourceNotFoundException("Usuario no encontrado") }
        return usuario.cliente?.idCliente
            ?: throw ResourceNotFoundException("El usuario autenticado no es un cliente")
    }

    fun getClienteIdOrNull(authentication: Authentication): Int? {
        val idUsuario = authentication.principal as Int
        val usuario = usuarioRepository.findById(idUsuario).orElse(null) ?: return null
        return usuario.cliente?.idCliente
    }
}
