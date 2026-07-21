package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "consulta_medica")
class ConsultaMedica : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consulta")
    var idConsulta: Int? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    lateinit var cita: Cita

    @Column(precision = 6, scale = 2)
    var peso: BigDecimal? = null

    @Column(precision = 4, scale = 2)
    var temperatura: BigDecimal? = null

    @Column(nullable = false, columnDefinition = "TEXT")
    lateinit var sintomas: String

    @Column(name = "diagnostico_general", columnDefinition = "TEXT")
    var diagnosticoGeneral: String? = null

    @Column(name = "tratamiento_indicado", columnDefinition = "TEXT")
    var tratamientoIndicado: String? = null

    @Column(columnDefinition = "TEXT")
    var observaciones: String? = null

    @Column(name = "fecha_consulta", nullable = false)
    var fechaConsulta: LocalDateTime = LocalDateTime.now()
}
