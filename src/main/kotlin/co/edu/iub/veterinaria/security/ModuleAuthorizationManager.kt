package co.edu.iub.veterinaria.security

import co.edu.iub.veterinaria.repository.RolModuloRepository
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ModuleAuthorizationManager(
    private val rolModuloRepository: RolModuloRepository
) {

    private val cache = ConcurrentHashMap<String, Set<String>>()

    fun hasModule(module: String): AuthorizationManager<RequestAuthorizationContext> =
        hasAnyModule(module)

    fun hasAnyModule(vararg modulos: String): AuthorizationManager<RequestAuthorizationContext> =
        AuthorizationManager { authenticationSupplier, _ ->
            AuthorizationDecision(tieneModulo(authenticationSupplier.get(), *modulos))
        }

    fun tieneModulo(authentication: Authentication?, vararg modulos: String): Boolean {
        if (authentication == null ||
            !authentication.isAuthenticated ||
            authentication is AnonymousAuthenticationToken
        ) {
            return false
        }
        val roles = authentication.authorities.map { it.authority.removePrefix("ROLE_") }
        if (roles.contains("ADMIN")) {
            return true
        }
        return modulos.any { modulo ->
            roles.any { rol -> modulosDeRol(rol).contains(modulo) }
        }
    }

    private fun modulosDeRol(nombreRol: String): Set<String> =
        cache.getOrPut(nombreRol) {
            val configurados = rolModuloRepository.findModuloNombresByRolNombre(nombreRol).toSet()
            if (configurados.isEmpty()) {
                DEFAULTES_POR_ROL[nombreRol] ?: emptySet()
            } else {
                configurados
            }
        }

    fun invalidar() = cache.clear()

    companion object {
        private val DEFAULTES_POR_ROL = mapOf(
            "CLIENTE" to setOf("MASCOTAS", "CITAS", "FACTURACION", "CALIFICACIONES"),
            "RECEPCIONISTA" to setOf("CLIENTES", "MASCOTAS", "CITAS", "FACTURACION"),
            "VETERINARIO" to setOf("MASCOTAS", "CITAS", "HISTORIAL"),
            "ESTILISTA" to setOf("MASCOTAS", "CITAS", "HISTORIAL")
        )
    }
}