package co.edu.iub.veterinaria.security

import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = extractToken(request)

            if (token != null && jwtTokenProvider.validateToken(token)) {
                val idUsuario = jwtTokenProvider.getIdUsuarioFromToken(token)
                val roles = jwtTokenProvider.getRolesFromToken(token)

                val authorities = roles.map { SimpleGrantedAuthority("ROLE_$it") }
                val authentication = UsernamePasswordAuthenticationToken(
                    idUsuario, null, authorities
                )
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: JwtException) {
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization")
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7)
        }
        return null
    }
}
