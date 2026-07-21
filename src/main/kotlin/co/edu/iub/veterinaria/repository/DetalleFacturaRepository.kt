package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.DetalleFactura
import org.springframework.data.jpa.repository.JpaRepository

interface DetalleFacturaRepository : JpaRepository<DetalleFactura, Int> {
    fun findByFacturaIdFactura(idFactura: Int): List<DetalleFactura>
}
