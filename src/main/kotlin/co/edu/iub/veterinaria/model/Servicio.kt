package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "servicio")
class Servicio : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
    var idServicio: Int? = null

    @Column(nullable = false, unique = true, length = 100)
    lateinit var nombre: String

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_servicio", nullable = false, length = 20)
    lateinit var tipoServicio: TipoServicio

    @Column(nullable = false, precision = 10, scale = 2)
    var precio: BigDecimal = BigDecimal.ZERO
}

enum class TipoServicio {
    CONSULTA, ESTETICA, OTRO
}
