package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "pago")
class Pago : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    var idPago: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura", nullable = false)
    lateinit var factura: Factura

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    lateinit var metodoPago: MetodoPago

    @Column(name = "fecha_pago", nullable = false)
    var fechaPago: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false, precision = 10, scale = 2)
    var monto: BigDecimal = BigDecimal.ZERO

    @Column(name = "referencia_pago", length = 100)
    var referenciaPago: String? = null
}
