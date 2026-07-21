package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "password_reset_token")
class PasswordResetToken : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token")
    var idToken: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    lateinit var usuario: Usuario

    @Column(nullable = false, unique = true, length = 255)
    lateinit var token: String

    @Column(name = "fecha_expiracion", nullable = false)
    lateinit var fechaExpiracion: LocalDateTime

    @Column(nullable = false)
    var usado: Boolean = false
}
