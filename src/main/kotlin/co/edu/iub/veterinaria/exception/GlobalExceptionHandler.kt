package co.edu.iub.veterinaria.exception

import co.edu.iub.veterinaria.dto.error.ErrorResponse
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(
        ex: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = HttpStatus.NOT_FOUND.reasonPhrase,
                message = ex.message ?: "Recurso no encontrado.",
                path = request.requestURI
            ),
            HttpStatus.NOT_FOUND
        )
    }

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicate(
        ex: DuplicateResourceException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.CONFLICT.value(),
                error = HttpStatus.CONFLICT.reasonPhrase,
                message = ex.message ?: "El registro ya existe.",
                path = request.requestURI
            ),
            HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val mensaje = ex.bindingResult
            .fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }

        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = mensaje,
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("Error de lectura HTTP en {}: {}", request.requestURI, ex.message, ex)
        val mensaje = when (val cause = ex.cause) {
            is InvalidFormatException -> {
                val campo = cause.path.firstOrNull()?.fieldName
                val valores = when (campo) {
                    "tipoDocumento" -> "CC, CE, TI, PASAPORTE"
                    "tipoServicio" -> "CONSULTA, ESTETICA, OTRO"
                    "sexo" -> "MACHO, HEMBRA"
                    "tipoHistorial" -> "MEDICO, ESTETICA"
                    "estadoCita" -> "PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA"
                    "estadoFactura" -> "PENDIENTE, PAGADA, ANULADA"
                    else -> null
                }
                if (valores != null) "Valor inválido para '$campo'. Permitidos: $valores."
                else "Valor inválido para el campo '$campo'."
            }
            else -> "El cuerpo de la solicitud contiene datos inválidos."
        }

        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = mensaje,
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = ex.message ?: "Solicitud inválida.",
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(
        ex: DataIntegrityViolationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.warn("Violación de integridad de datos en {}: {}", request.requestURI, ex.mostSpecificCause.message)
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.CONFLICT.value(),
                error = HttpStatus.CONFLICT.reasonPhrase,
                message = "La operación viola una restricción única en la base de datos.",
                path = request.requestURI
            ),
            HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(
        ex: BadCredentialsException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                error = HttpStatus.UNAUTHORIZED.reasonPhrase,
                message = ex.message ?: "Credenciales inválidas.",
                path = request.requestURI
            ),
            HttpStatus.UNAUTHORIZED
        )
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(
        ex: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.FORBIDDEN.value(),
                error = HttpStatus.FORBIDDEN.reasonPhrase,
                message = "No tiene permisos para acceder a este recurso.",
                path = request.requestURI
            ),
            HttpStatus.FORBIDDEN
        )
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(
        ex: MissingServletRequestParameterException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = "El parámetro '${ex.parameterName}' es obligatorio.",
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        val tipo = ex.requiredType
        val mensaje = if (tipo != null && tipo.isEnum) {
            val valores = tipo.enumConstants.joinToString(", ")
            "Valor inválido para '${ex.name}'. Permitidos: $valores."
        } else {
            "Valor inválido para el parámetro '${ex.name}'."
        }

        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = HttpStatus.BAD_REQUEST.reasonPhrase,
                message = mensaje,
                path = request.requestURI
            ),
            HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneral(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("Error no controlado en {}: {}", request.requestURI, ex.message, ex)

        return ResponseEntity(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase,
                message = "Ha ocurrido un error inesperado.",
                path = request.requestURI
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
