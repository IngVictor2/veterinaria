package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Calificacion
import org.springframework.data.jpa.repository.JpaRepository

interface CalificacionRepository : JpaRepository<Calificacion, Int> {
    fun findByCitaIdCita(idCita: Int): Calificacion?
    fun findByCitaMascotaClienteIdCliente(idCliente: Int): List<Calificacion>
}
