package co.edu.iub.veterinaria.model

import jakarta.persistence.*

@Entity
@Table(name = "rol")
class Rol : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    var idRol: Int? = null

    @Column(nullable = false, unique = true, length = 50)
    lateinit var nombre: String

    @Column(columnDefinition = "TEXT")
    var descripcion: String? = null
}
