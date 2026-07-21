package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "factura")
class Factura : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    var idFactura: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    lateinit var cliente: Cliente

    @Column(name = "fecha_factura", nullable = false)
    var fechaFactura: LocalDate = LocalDate.now()

    @Column(nullable = false, precision = 10, scale = 2)
    var subtotal: BigDecimal = BigDecimal.ZERO

    @Column(nullable = false, precision = 10, scale = 2)
    var descuento: BigDecimal = BigDecimal.ZERO

    @Column(nullable = false, precision = 10, scale = 2)
    var total: BigDecimal = BigDecimal.ZERO

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_factura", nullable = false, length = 20)
    var estadoFactura: EstadoFactura = EstadoFactura.PENDIENTE
}

enum class EstadoFactura {
    PENDIENTE, PAGADA, ANULADA
}
