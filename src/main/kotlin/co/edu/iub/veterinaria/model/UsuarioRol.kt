package co.edu.iub.veterinaria.model

import jakarta.persistence.*

@Entity
@Table(
    name = "usuario_rol",
    uniqueConstraints = [UniqueConstraint(columnNames = ["id_usuario", "id_rol"])]
)
class UsuarioRol : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario_rol")
    var idUsuarioRol: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    lateinit var usuario: Usuario

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    lateinit var rol: Rol
}
