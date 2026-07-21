package co.edu.iub.veterinaria.model

import jakarta.persistence.*

@Entity
@Table(
    name = "rol_modulo",
    uniqueConstraints = [UniqueConstraint(columnNames = ["id_rol", "id_modulo"])]
)
class RolModulo : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol_modulo")
    var idRolModulo: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    lateinit var rol: Rol

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_modulo", nullable = false)
    lateinit var modulo: Modulo
}
