package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(
    name = "cita",
    uniqueConstraints = [UniqueConstraint(columnNames = ["id_empleado", "fecha_cita", "hora_cita"])]
)
class Cita : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    var idCita: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mascota", nullable = false)
    lateinit var mascota: Mascota

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false)
    lateinit var empleado: Empleado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false)
    lateinit var servicio: Servicio

    @Column(name = "fecha_cita", nullable = false)
    lateinit var fechaCita: LocalDate

    @Column(name = "hora_cita", nullable = false)
    lateinit var horaCita: LocalTime

    @Column(length = 200)
    var motivo: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cita", nullable = false, length = 20)
    var estadoCita: EstadoCita = EstadoCita.PENDIENTE

    @Column(columnDefinition = "TEXT")
    var observaciones: String? = null
}

enum class EstadoCita {
    PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA
}
