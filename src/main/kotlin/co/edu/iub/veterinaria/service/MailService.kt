package co.edu.iub.veterinaria.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailService(
    private val mailSender: JavaMailSender,
    @Value("\${app.mail.from}") private val from: String,
    @Value("\${app.mail.reset-url-base}") private val resetUrlBase: String
) {

    private val log = LoggerFactory.getLogger(MailService::class.java)

    fun sendPasswordResetEmail(correo: String, token: String) {
        if (from.isBlank()) {
            log.error("MAIL_FROM no configurado; no se envio el correo de recuperacion a {}", correo)
            return
        }

        try {
            val url = "$resetUrlBase?token=$token"

            val message = SimpleMailMessage().apply {
                setFrom(from)
                setTo(correo)
                subject = "Recuperacion de contrasena - Veterinaria"
                text = """
                    Hola,

                    Hemos recibido una solicitud para restablecer tu contrasena.

                    Ingresa al siguiente enlace para crear una nueva contrasena (expira en 1 hora):

                    $url

                    Si no solicitaste este cambio, ignora este correo.

                    - Sistema Veterinaria
                """.trimIndent()
            }

            mailSender.send(message)
            log.info("Correo de recuperacion enviado a {}", correo)
        } catch (e: Exception) {
            log.error("No se pudo enviar el correo de recuperacion a {}: {}", correo, e.message, e)
        }
    }
}
