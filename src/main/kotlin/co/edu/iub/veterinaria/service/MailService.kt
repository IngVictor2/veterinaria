package co.edu.iub.veterinaria.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient

@Service
class MailService(
    @Value("\${app.mail.api-key}") private val apiKey: String,
    @Value("\${app.mail.from}") private val from: String,
    @Value("\${app.mail.reset-url-base}") private val resetUrlBase: String
) {

    private val log = LoggerFactory.getLogger(MailService::class.java)

    private val restClient = RestClient.builder()
        .baseUrl("https://api.sendgrid.com/v3")
        .defaultHeader("Authorization", "Bearer $apiKey")
        .build()

    fun sendPasswordResetEmail(correo: String, token: String) {
        if (apiKey.isBlank() || from.isBlank()) {
            log.error("MAIL_API_KEY o MAIL_FROM no configurados; no se envio el correo de recuperacion a {}", correo)
            return
        }

        try {
            val url = "$resetUrlBase?token=$token"

            val body = mapOf(
                "personalizations" to listOf(mapOf("to" to listOf(mapOf("email" to correo)))),
                "from" to mapOf("email" to from),
                "subject" to "Recuperacion de contrasena - Veterinaria",
                "content" to listOf(
                    mapOf(
                        "type" to "text/html",
                        "value" to """
                            <p>Hola,</p>
                            <p>Hemos recibido una solicitud para restablecer tu contrasena.</p>
                            <p>Ingresa al siguiente enlace para crear una nueva contrasena (expira en 1 hora):</p>
                            <p><a href="$url">$url</a></p>
                            <p>Si no solicitaste este cambio, ignora este correo.</p>
                            <p>- Sistema Veterinaria</p>
                        """.trimIndent()
                    )
                )
            )

            restClient.post()
                .uri("/mail/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toBodilessEntity()

            log.info("Correo de recuperacion enviado a {}", correo)
        } catch (e: Exception) {
            log.error("No se pudo enviar el correo de recuperacion a {}: {}", correo, e.message, e)
        }
    }
}
