package co.edu.iub.veterinaria.model

import jakarta.persistence.*

@Entity
@Table(name = "modulo")
class Modulo : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modulo")
    var idModulo: Int? = null

    @Column(nullable = false, unique = true, length = 50)
    lateinit var nombre: String

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null
}
