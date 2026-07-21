package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.RolModulo
import org.springframework.data.jpa.repository.JpaRepository

interface RolModuloRepository : JpaRepository<RolModulo, Int> {
    fun findByRolIdRol(idRol: Int): List<RolModulo>
    fun existsByRolIdRolAndModuloIdModulo(idRol: Int, idModulo: Int): Boolean
}
