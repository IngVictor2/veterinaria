package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Empleado
import org.springframework.data.jpa.repository.JpaRepository

interface EmpleadoRepository : JpaRepository<Empleado, Int> {
    fun existsByNumeroDocumento(numeroDocumento: String): Boolean
    fun existsByCorreo(correo: String): Boolean
}
