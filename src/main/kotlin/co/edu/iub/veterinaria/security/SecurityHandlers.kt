package co.edu.iub.veterinaria.security

import co.edu.iub.veterinaria.dto.error.ErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler

class RestAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val error = ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = HttpStatus.UNAUTHORIZED.reasonPhrase,
            message = "Token inválido o no proporcionado.",
            path = request.requestURI
        )
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write(objectMapper.writeValueAsString(error))
    }
}

class RestAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        val error = ErrorResponse(
            status = HttpStatus.FORBIDDEN.value(),
            error = HttpStatus.FORBIDDEN.reasonPhrase,
            message = "No tiene permisos para acceder a este recurso.",
            path = request.requestURI
        )
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.writer.write(objectMapper.writeValueAsString(error))
    }
}
