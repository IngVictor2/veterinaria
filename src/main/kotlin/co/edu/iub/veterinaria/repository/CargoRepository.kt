package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Cargo
import org.springframework.data.jpa.repository.JpaRepository

interface CargoRepository : JpaRepository<Cargo, Int> {
    fun findByNombre(nombre: String): Cargo?
}
