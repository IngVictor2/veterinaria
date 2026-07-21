package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.ServicioEstetica
import org.springframework.data.jpa.repository.JpaRepository

interface ServicioEsteticaRepository : JpaRepository<ServicioEstetica, Int> {
    fun findByCitaIdCita(idCita: Int): ServicioEstetica?
}
