package co.edu.iub.veterinaria.security

import co.edu.iub.veterinaria.repository.UsuarioRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class CurrentUserHelper(
    private val usuarioRepository: UsuarioRepository
) {
    fun getClienteIdOrNull(authentication: Authentication): Int? {
        val idUsuario = authentication.principal as Int
        val usuario = usuarioRepository.findById(idUsuario).orElse(null) ?: return null
        return usuario.cliente?.idCliente
    }

    fun getClienteIdContextual(authentication: Authentication): Int? =
        if (esPersonal(authentication)) null else getClienteIdOrNull(authentication)

    fun esPersonal(authentication: Authentication): Boolean =
        authentication.authorities.any { it.authority in ROLES_PERSONAL }

    companion object {
        private val ROLES_PERSONAL = setOf(
            "ROLE_ADMIN", "ROLE_VETERINARIO", "ROLE_ESTILISTA", "ROLE_RECEPCIONISTA"
        )
    }
}
