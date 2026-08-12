package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Mascota
import org.springframework.data.jpa.repository.JpaRepository

interface MascotaRepository : JpaRepository<Mascota, Int> {
    fun findByClienteIdClienteAndEstadoTrue(idCliente: Int): List<Mascota>
    fun findByClienteIdCliente(idCliente: Int): List<Mascota>
    fun countByClienteIdClienteAndEstadoTrue(idCliente: Int): Long
}
