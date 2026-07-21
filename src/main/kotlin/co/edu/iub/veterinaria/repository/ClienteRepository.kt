package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Cliente
import org.springframework.data.jpa.repository.JpaRepository

interface ClienteRepository : JpaRepository<Cliente, Int> {

    fun existsByNumeroDocumento(numeroDocumento: String): Boolean

    fun existsByCorreo(correo: String): Boolean

}