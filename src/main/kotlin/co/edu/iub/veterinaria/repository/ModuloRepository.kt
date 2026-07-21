package co.edu.iub.veterinaria.repository

import co.edu.iub.veterinaria.model.Modulo
import org.springframework.data.jpa.repository.JpaRepository

interface ModuloRepository : JpaRepository<Modulo, Int>
