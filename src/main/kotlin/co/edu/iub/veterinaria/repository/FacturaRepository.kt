package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Factura
import org.springframework.data.jpa.repository.JpaRepository

interface FacturaRepository : JpaRepository<Factura, Int> {
    fun findByClienteIdCliente(idCliente: Int): List<Factura>
}
