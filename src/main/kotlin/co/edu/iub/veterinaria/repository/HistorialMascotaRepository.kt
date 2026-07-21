package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.HistorialMascota
import org.springframework.data.jpa.repository.JpaRepository

interface HistorialMascotaRepository : JpaRepository<HistorialMascota, Int> {
    fun findByMascotaIdMascota(idMascota: Int): List<HistorialMascota>
}
