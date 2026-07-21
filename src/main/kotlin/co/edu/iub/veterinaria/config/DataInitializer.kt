package co.edu.iub.veterinaria.config

import co.edu.iub.veterinaria.model.*
import co.edu.iub.veterinaria.repository.*
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Component
class DataInitializer(
    private val rolRepository: RolRepository,
    private val moduloRepository: ModuloRepository,
    private val rolModuloRepository: RolModuloRepository,
    private val cargoRepository: CargoRepository,
    private val servicioRepository: ServicioRepository,
    private val metodoPagoRepository: MetodoPagoRepository,
    private val empleadoRepository: EmpleadoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val usuarioRolRepository: UsuarioRolRepository,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        if (rolRepository.count() > 0) return

        val rolAdmin = rolRepository.save(Rol().apply { nombre = "ADMIN"; descripcion = "Administrador del sistema" })
        val rolVet = rolRepository.save(Rol().apply { nombre = "VETERINARIO"; descripcion = "Médico veterinario" })
        val rolEst = rolRepository.save(Rol().apply { nombre = "ESTILISTA"; descripcion = "Estilista canino" })
        val rolRec = rolRepository.save(Rol().apply { nombre = "RECEPCIONISTA"; descripcion = "Recepcionista" })
        val rolCli = rolRepository.save(Rol().apply { nombre = "CLIENTE"; descripcion = "Cliente dueño de mascotas" })

        val modClientes = moduloRepository.save(Modulo().apply { nombre = "CLIENTES"; descripcion = "Gestión de clientes" })
        val modMascotas = moduloRepository.save(Modulo().apply { nombre = "MASCOTAS"; descripcion = "Gestión de mascotas" })
        val modCitas = moduloRepository.save(Modulo().apply { nombre = "CITAS"; descripcion = "Gestión de citas" })
        val modFacturas = moduloRepository.save(Modulo().apply { nombre = "FACTURACION"; descripcion = "Facturación y pagos" })
        val modHistorial = moduloRepository.save(Modulo().apply { nombre = "HISTORIAL"; descripcion = "Historial médico" })
        val modUsuarios = moduloRepository.save(Modulo().apply { nombre = "USUARIOS"; descripcion = "Gestión de usuarios" })
        val modRoles = moduloRepository.save(Modulo().apply { nombre = "ROLES"; descripcion = "Gestión de roles y permisos" })
        val modTarifas = moduloRepository.save(Modulo().apply { nombre = "TARIFAS"; descripcion = "Gestión de tarifas" })
        val modCalif = moduloRepository.save(Modulo().apply { nombre = "CALIFICACIONES"; descripcion = "Calificaciones" })

        fun asignar(rol: Rol, modulos: List<Modulo>) {
            modulos.forEach { mod ->
                rolModuloRepository.save(RolModulo().apply { this.rol = rol; this.modulo = mod })
            }
        }

        asignar(rolAdmin, listOf(modClientes, modMascotas, modCitas, modFacturas, modHistorial, modUsuarios, modRoles, modTarifas, modCalif))
        asignar(rolVet, listOf(modMascotas, modCitas, modHistorial))
        asignar(rolEst, listOf(modMascotas, modCitas, modHistorial))
        asignar(rolRec, listOf(modClientes, modMascotas, modCitas, modFacturas))
        asignar(rolCli, listOf(modMascotas, modCitas, modFacturas, modCalif))

        cargoRepository.save(Cargo().apply { nombre = "VETERINARIO"; descripcion = "Médico veterinario" })
        cargoRepository.save(Cargo().apply { nombre = "ESTILISTA"; descripcion = "Estilista" })
        cargoRepository.save(Cargo().apply { nombre = "RECEPCIONISTA"; descripcion = "Recepcionista" })
        cargoRepository.save(Cargo().apply { nombre = "ADMINISTRADOR"; descripcion = "Administrador" })

        servicioRepository.save(Servicio().apply { nombre = "Consulta General"; tipoServicio = TipoServicio.CONSULTA; precio = BigDecimal("50000") })
        servicioRepository.save(Servicio().apply { nombre = "Consulta Especializada"; tipoServicio = TipoServicio.CONSULTA; precio = BigDecimal("80000") })
        servicioRepository.save(Servicio().apply { nombre = "Vacunación"; tipoServicio = TipoServicio.CONSULTA; precio = BigDecimal("35000") })
        servicioRepository.save(Servicio().apply { nombre = "Baño Medicado"; tipoServicio = TipoServicio.ESTETICA; precio = BigDecimal("40000") })
        servicioRepository.save(Servicio().apply { nombre = "Corte de Pelo"; tipoServicio = TipoServicio.ESTETICA; precio = BigDecimal("35000") })
        servicioRepository.save(Servicio().apply { nombre = "Limpieza Dental"; tipoServicio = TipoServicio.ESTETICA; precio = BigDecimal("60000") })
        servicioRepository.save(Servicio().apply { nombre = "Cirugía Menor"; tipoServicio = TipoServicio.OTRO; precio = BigDecimal("150000") })

        metodoPagoRepository.save(MetodoPago().apply { nombre = "EFECTIVO"; descripcion = "Pago en efectivo" })
        metodoPagoRepository.save(MetodoPago().apply { nombre = "TARJETA_DEBITO"; descripcion = "Tarjeta débito" })
        metodoPagoRepository.save(MetodoPago().apply { nombre = "TARJETA_CREDITO"; descripcion = "Tarjeta crédito" })
        metodoPagoRepository.save(MetodoPago().apply { nombre = "TRANSFERENCIA"; descripcion = "Transferencia bancaria" })

        val cargoAdmin = cargoRepository.findByNombre("ADMINISTRADOR")!!

        val admin = empleadoRepository.save(Empleado().apply {
            this.cargo = cargoAdmin
            tipoDocumento = TipoDocumento.CC
            numeroDocumento = "0000000001"
            primerNombre = "Admin"
            primerApellido = "Sistema"
            correo = "admin@veterinaria.com"
            fechaIngreso = LocalDate.now()
        })

        val adminUser = usuarioRepository.save(Usuario().apply {
            this.empleado = admin
            nombreUsuario = "admin"
            passwordHash = passwordEncoder.encode("admin123")
        })

        usuarioRolRepository.save(UsuarioRol().apply {
            this.usuario = adminUser
            this.rol = rolAdmin
        })
    }
}
