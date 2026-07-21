package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "mascota")
class Mascota : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mascota")
    var idMascota: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    lateinit var cliente: Cliente

    @Column(nullable = false, length = 80)
    lateinit var nombre: String

    @Column(nullable = false, length = 50)
    lateinit var especie: String

    @Column(length = 80)
    var raza: String? = null

    @Column(nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    lateinit var sexo: SexoMascota

    @Column(name = "fecha_nacimiento")
    var fechaNacimiento: LocalDate? = null

    @Column(precision = 5, scale = 2)
    var peso: BigDecimal? = null

    @Column(columnDefinition = "TEXT")
    var observaciones: String? = null
}

enum class SexoMascota {
    MACHO, HEMBRA
}
