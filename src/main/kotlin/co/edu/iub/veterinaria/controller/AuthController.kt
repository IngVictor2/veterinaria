package co.edu.iub.veterinaria.controller

import co.edu.iub.veterinaria.dto.auth.*
import co.edu.iub.veterinaria.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse {
        return authService.login(request)
    }

    @PostMapping("/logout")
    fun logout(): Map<String, String> {
        return mapOf("mensaje" to "Sesion cerrada. Invalide el token en el cliente.")
    }

    @PostMapping("/cambiar-password")
    fun changePassword(
        authentication: Authentication,
        @Valid @RequestBody request: ChangePasswordRequest
    ) {
        val idUsuario = authentication.principal as Int
        authService.changePassword(idUsuario, request)
    }

    @PostMapping("/recuperar-password")
    fun requestReset(@Valid @RequestBody request: ResetPasswordRequest) {
        authService.requestPasswordReset(request)
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestParam token: String,
        @Valid @RequestBody request: NewPasswordRequest
    ) {
        authService.resetPassword(token, request.password)
    }
}
