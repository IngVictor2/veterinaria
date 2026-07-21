package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UsuarioRepository : JpaRepository<Usuario, Int> {
    fun findByNombreUsuario(nombreUsuario: String): Usuario?

    @Query("""
        SELECT u FROM Usuario u
        LEFT JOIN FETCH u.cliente c
        LEFT JOIN FETCH u.empleado e
        WHERE c.correo = :correo OR e.correo = :correo
    """)
    fun findByCorreo(correo: String): Usuario?

    fun existsByNombreUsuario(nombreUsuario: String): Boolean

    @Query("SELECT COALESCE(c.correo, e.correo) FROM Usuario u " +
           "LEFT JOIN u.cliente c LEFT JOIN u.empleado e WHERE u.idUsuario = :idUsuario")
    fun findCorreoByIdUsuario(idUsuario: Int): String?

    fun findByEmpleadoIdEmpleado(idEmpleado: Int): Usuario?
}
