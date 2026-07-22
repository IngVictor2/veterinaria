package co.edu.iub.veterinaria.exception

import org.springframework.http.HttpStatus

class ResourceNotFoundException(
    message: String
) : ApiException(message, HttpStatus.NOT_FOUND)