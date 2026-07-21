package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.ConsultaMedica
import org.springframework.data.jpa.repository.JpaRepository

interface ConsultaMedicaRepository : JpaRepository<ConsultaMedica, Int> {
    fun findByCitaIdCita(idCita: Int): ConsultaMedica?
}
