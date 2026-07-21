package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Servicio
import co.edu.iub.veterinaria.model.TipoServicio
import org.springframework.data.jpa.repository.JpaRepository

interface ServicioRepository : JpaRepository<Servicio, Int> {
    fun findByTipoServicio(tipoServicio: TipoServicio): List<Servicio>
    fun existsByNombreIgnoreCase(nombre: String): Boolean
}
