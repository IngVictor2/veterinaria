package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.admin.AdminPasswordRequest
import co.edu.iub.veterinaria.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/usuarios")
class AdminPasswordController(
    private val authService: AuthService
) {
    @PostMapping("/{idUsuario}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cambiarPassword(@PathVariable idUsuario: Int, @Valid @RequestBody request: AdminPasswordRequest) {
        authService.cambiarPasswordAdmin(idUsuario, request.nuevaPassword)
    }
}
