package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Rol
import org.springframework.data.jpa.repository.JpaRepository

interface RolRepository : JpaRepository<Rol, Int> {
    fun findByNombre(nombre: String): Rol?
}
