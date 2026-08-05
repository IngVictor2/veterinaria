package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.cita.*
import co.edu.iub.veterinaria.exception.InvalidRequestException
import co.edu.iub.veterinaria.model.EstadoCita
import co.edu.iub.veterinaria.security.CurrentUserHelper
import co.edu.iub.veterinaria.service.CitaService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/citas")
class CitaController(
    private val citaService: CitaService,
    private val currentUserHelper: CurrentUserHelper
) {
    @GetMapping
    fun listar(): List<CitaResponse> = citaService.listar()

    @GetMapping("/mis-citas")
    fun misCitas(authentication: Authentication): List<CitaResponse> {
        val idCliente = currentUserHelper.getClienteIdOrNull(authentication) ?: return emptyList()
        return citaService.listarPorCliente(idCliente)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Int, authentication: Authentication): CitaResponse {
        val idCliente = currentUserHelper.getClienteIdOrNull(authentication)
        return citaService.buscarPorId(id, idCliente)
    }

    @GetMapping("/cliente/{idCliente}")
    fun listarPorCliente(@PathVariable idCliente: Int, authentication: Authentication): List<CitaResponse> {
        val idClienteActual = currentUserHelper.getClienteIdOrNull(authentication)
        if (idClienteActual != null && idClienteActual != idCliente) {
            throw AccessDeniedException("No tiene permisos para ver estas citas")
        }
        return citaService.listarPorCliente(idCliente)
    }

    @GetMapping("/mascota/{idMascota}")
    fun listarPorMascota(@PathVariable idMascota: Int, authentication: Authentication): List<CitaResponse> {
        val idCliente = currentUserHelper.getClienteIdOrNull(authentication)
        return citaService.listarPorMascota(idMascota, idCliente)
    }

    @GetMapping("/empleado/{idEmpleado}")
    fun listarPorEmpleado(@PathVariable idEmpleado: Int): List<CitaResponse> =
        citaService.listarPorEmpleado(idEmpleado)

    @GetMapping("/disponibilidad/{fecha}")
    fun disponibilidad(@PathVariable fecha: LocalDate): List<CitaResponse> =
        citaService.listarPorFecha(fecha)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun crear(@Valid @RequestBody request: CitaRequest): CitaResponse =
        citaService.crear(request)

    @PutMapping("/{id}")
    fun reprogramar(@PathVariable id: Int, @Valid @RequestBody request: CitaRequest): CitaResponse =
        citaService.reprogramar(id, request)

    @PatchMapping("/{id}/estado")
    fun cambiarEstado(@PathVariable id: Int, @RequestBody body: Map<String, String>) {
        val estadoStr = body["estadoCita"] ?: throw InvalidRequestException("estadoCita es requerido")
        val estado = EstadoCita.valueOf(estadoStr)
        citaService.cambiarEstado(id, estado)
    }

    @PostMapping("/{id}/cancelar")
    fun cancelar(@PathVariable id: Int) = citaService.cancelar(id)

    @PostMapping("/consulta")
    @ResponseStatus(HttpStatus.CREATED)
    fun registrarConsulta(@Valid @RequestBody request: ConsultaRequest) =
        citaService.registrarConsulta(request)

    @PostMapping("/estetica")
    @ResponseStatus(HttpStatus.CREATED)
    fun registrarEstetica(@Valid @RequestBody request: EsteticaRequest) =
        citaService.registrarEstetica(request)
}
