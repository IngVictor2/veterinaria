package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.RolModulo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface RolModuloRepository : JpaRepository<RolModulo, Int> {
    fun findByRolIdRol(idRol: Int): List<RolModulo>
    fun findByRolIdRolAndEstadoTrue(idRol: Int): List<RolModulo>
    fun existsByRolIdRolAndModuloIdModulo(idRol: Int, idModulo: Int): Boolean

    @Query("SELECT rm.modulo.nombre FROM RolModulo rm WHERE rm.rol.nombre = :nombreRol AND rm.estado = true")
    fun findModuloNombresByRolNombre(nombreRol: String): List<String>
}
