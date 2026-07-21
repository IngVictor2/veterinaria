package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository

interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Int> {
    fun findByToken(token: String): PasswordResetToken?
}
