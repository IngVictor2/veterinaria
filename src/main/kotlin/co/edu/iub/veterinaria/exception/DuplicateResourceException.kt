package co.edu.iub.veterinaria.exception

import org.springframework.http.HttpStatus

class DuplicateResourceException(
    message: String
) : ApiException(message, HttpStatus.CONFLICT)