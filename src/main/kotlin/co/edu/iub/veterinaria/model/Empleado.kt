package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "empleado")
class Empleado : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    var idEmpleado: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cargo", nullable = false)
    lateinit var cargo: Cargo

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    lateinit var tipoDocumento: TipoDocumento

    @Column(name = "numero_documento", nullable = false, unique = true, length = 30)
    lateinit var numeroDocumento: String

    @Column(name = "primer_nombre", nullable = false, length = 80)
    lateinit var primerNombre: String

    @Column(name = "segundo_nombre", length = 80)
    var segundoNombre: String? = null

    @Column(name = "primer_apellido", nullable = false, length = 80)
    lateinit var primerApellido: String

    @Column(name = "segundo_apellido", length = 80)
    var segundoApellido: String? = null

    @Column(length = 20)
    var telefono: String? = null

    @Column(length = 100, unique = true)
    var correo: String? = null

    @Column(length = 150)
    var direccion: String? = null

    @Column(name = "fecha_ingreso", nullable = false)
    lateinit var fechaIngreso: LocalDate
}
