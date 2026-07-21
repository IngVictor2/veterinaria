package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.UsuarioRol
import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioRolRepository : JpaRepository<UsuarioRol, Int> {
    fun findByUsuarioIdUsuarioAndEstadoTrue(idUsuario: Int): List<UsuarioRol>
    fun findByUsuarioIdUsuario(idUsuario: Int): List<UsuarioRol>
    fun existsByUsuarioIdUsuarioAndRolIdRol(idUsuario: Int, idRol: Int): Boolean
}
