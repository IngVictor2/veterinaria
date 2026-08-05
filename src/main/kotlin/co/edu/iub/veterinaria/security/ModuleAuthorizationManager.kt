package co.edu.iub.veterinaria.security

import co.edu.iub.veterinaria.repository.RolModuloRepository
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class ModuleAuthorizationManager(
    private val rolModuloRepository: RolModuloRepository
) {

    private val cache = ConcurrentHashMap<String, Set<String>>()

    fun hasModule(module: String): AuthorizationManager<RequestAuthorizationContext> =
        AuthorizationManager { authenticationSupplier, _ ->
            val authentication = authenticationSupplier.get()
            if (authentication == null || !authentication.isAuthenticated) {
                return@AuthorizationManager AuthorizationDecision(false)
            }
            val roles = authentication.authorities.map { it.authority.removePrefix("ROLE_") }
            if (roles.contains("ADMIN")) {
                return@AuthorizationManager AuthorizationDecision(true)
            }
            AuthorizationDecision(roles.any { modulosDeRol(it).contains(module) })
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
            "RECEPCIONISTA" to setOf("CLIENTES", "MASCOTAS", "CITAS", "FACTURACION"),
            "VETERINARIO" to setOf("MASCOTAS", "CITAS", "HISTORIAL"),
            "ESTILISTA" to setOf("MASCOTAS", "CITAS", "HISTORIAL")
        )
    }
}