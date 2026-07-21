package co.edu.iub.veterinaria.model

import jakarta.persistence.*

@Entity
@Table(name = "servicio_estetica")
class ServicioEstetica : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio_estetica")
    var idServicioEstetica: Int? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    lateinit var cita: Cita

    @Column(columnDefinition = "TEXT")
    var detalles: String? = null

    @Column(columnDefinition = "TEXT")
    var observaciones: String? = null
}
