package co.edu.iub.veterinaria.model

import jakarta.persistence.*

@Entity
@Table(name = "calificacion")
class Calificacion : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_calificacion")
    var idCalificacion: Int? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false, unique = true)
    lateinit var cita: Cita

    @Column(nullable = false, columnDefinition = "SMALLINT")
    var puntuacion: Int = 5

    @Column(columnDefinition = "TEXT")
    var comentario: String? = null
}
