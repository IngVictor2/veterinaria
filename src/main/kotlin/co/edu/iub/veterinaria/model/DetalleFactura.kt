package co.edu.iub.veterinaria.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "detalle_factura")
class DetalleFactura : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_factura")
    var idDetalleFactura: Int? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura", nullable = false)
    lateinit var factura: Factura

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", nullable = false)
    lateinit var cita: Cita

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_servicio", nullable = false)
    lateinit var servicio: Servicio

    @Column(nullable = false)
    var cantidad: Int = 1

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    var precio: BigDecimal = BigDecimal.ZERO
}
