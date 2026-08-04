package co.edu.iub.veterinaria.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import java.util.Base64

@Service
class MailService(
    @Value("\${app.mail.api-key}") private val apiKey: String,
    @Value("\${app.mail.domain}") private val domain: String,
    @Value("\${app.mail.from}") private val from: String,
    @Value("\${app.mail.reset-url-base}") private val resetUrlBase: String
) {

    private val log = LoggerFactory.getLogger(MailService::class.java)

    private val restClient = RestClient.builder()
        .baseUrl("https://api.mailgun.net/v3")
        .defaultHeader(
            "Authorization",
            "Basic " + Base64.getEncoder().encodeToString("api:$apiKey".toByteArray())
        )
        .build()

    fun sendPasswordResetEmail(correo: String, token: String) {
        if (apiKey.isBlank() || domain.isBlank() || from.isBlank()) {
            log.error("MAIL_API_KEY, MAILGUN_DOMAIN o MAIL_FROM no configurados; no se envio el correo a {}", correo)
            return
        }

        try {
            val url = "$resetUrlBase?token=$token"

            val form = LinkedMultiValueMap<String, String>().apply {
                add("from", from)
                add("to", correo)
                add("subject", "Recuperacion de contrasena - Veterinaria")
                add("html", """
                    <p>Hola,</p>
                    <p>Hemos recibido una solicitud para restablecer tu contrasena.</p>
                    <p>Ingresa al siguiente enlace para crear una nueva contrasena (expira en 1 hora):</p>
                    <p><a href="$url">$url</a></p>
                    <p>Si no solicitaste este cambio, ignora este correo.</p>
                    <p>- Sistema Veterinaria</p>
                """.trimIndent())
            }

            restClient.post()
                .uri("/{domain}/messages", domain)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .toBodilessEntity()

            log.info("Correo de recuperacion enviado a {}", correo)
        } catch (e: Exception) {
            log.error("No se pudo enviar el correo de recuperacion a {}: {}", correo, e.message, e)
        }
    }
}
