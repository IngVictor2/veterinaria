package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Cita
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface CitaRepository : JpaRepository<Cita, Int> {
    fun findByMascotaIdMascota(idMascota: Int): List<Cita>
    fun findByMascotaClienteIdCliente(idCliente: Int): List<Cita>
    fun findByEmpleadoIdEmpleado(idEmpleado: Int): List<Cita>
    fun findByFechaCita(fecha: LocalDate): List<Cita>
}
