package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "historial_mascota")
class HistorialMascota : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    var idHistorial: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    lateinit var mascota: Mascota

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false)
    lateinit var cita: Cita

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consulta")
    var consulta: ConsultaMedica? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio_estetica")
    var servicioEstetica: ServicioEstetica? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_historial", nullable = false, length = 20)
    lateinit var tipoHistorial: TipoHistorial

    @Column(nullable = false, columnDefinition = "TEXT")
    lateinit var resumen: String

    @Column(name = "fecha_registro", nullable = false)
    var fechaRegistro: LocalDateTime = LocalDateTime.now()
}

enum class TipoHistorial {
    MEDICO, ESTETICA
}
