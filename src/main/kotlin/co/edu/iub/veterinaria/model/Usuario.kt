package co.edu.iub.veterinaria.model

import jakarta.persistence.*

@Entity
@Table(name = "usuario")
class Usuario : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    var idUsuario: Int? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    var cliente: Cliente? = null

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado")
    var empleado: Empleado? = null

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    lateinit var nombreUsuario: String

    @Column(name = "password_hash", nullable = false, length = 255)
    lateinit var passwordHash: String
}
