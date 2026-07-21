package co.edu.iub.veterinaria.security

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${app.jwt.secret}") private val jwtSecret: String,
    @Value("\${app.jwt.expiration-minutes}") private val jwtExpirationMinutes: Long
) {

    private val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    }

    fun generateToken(idUsuario: Int, nombreUsuario: String, roles: List<String>): String {
        val now = Date()
        val expiration = Date(now.time + jwtExpirationMinutes * 60 * 1000)

        return Jwts.builder()
            .subject(idUsuario.toString())
            .claim("nombreUsuario", nombreUsuario)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(key)
            .compact()
    }

    fun getIdUsuarioFromToken(token: String): Int {
        val claims = parseClaims(token)
        return try {
            claims.subject.toInt()
        } catch (e: NumberFormatException) {
            throw JwtException("Invalid subject in token")
        }
    }

    fun getRolesFromToken(token: String): List<String> {
        val claims = parseClaims(token)
        @Suppress("UNCHECKED_CAST")
        return claims.get("roles", List::class.java) as? List<String> ?: emptyList()
    }

    fun validateToken(token: String): Boolean {
        return try {
            parseClaims(token)
            true
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun parseClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
