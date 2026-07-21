package co.edu.iub.veterinaria.config

import co.edu.iub.veterinaria.security.JwtAuthenticationFilter
import co.edu.iub.veterinaria.security.RestAccessDeniedHandler
import co.edu.iub.veterinaria.security.RestAuthenticationEntryPoint
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val objectMapper: ObjectMapper
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling {
                it.authenticationEntryPoint(RestAuthenticationEntryPoint(objectMapper))
                it.accessDeniedHandler(RestAccessDeniedHandler(objectMapper))
            }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/").permitAll()
                    .requestMatchers("/auth/register", "/auth/login", "/auth/recuperar-password", "/auth/reset-password").permitAll()
                    .requestMatchers(HttpMethod.GET, "/servicios/**").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/empleados/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/servicios/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/servicios/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/servicios/**").hasRole("ADMIN")

                    .requestMatchers("/clientes/me").authenticated()
                    .requestMatchers("/clientes/**").hasAnyRole("RECEPCIONISTA", "ADMIN")

                    .requestMatchers("/mascotas/mis-mascotas").authenticated()
                    .requestMatchers("/mascotas/**").hasAnyRole("RECEPCIONISTA", "ADMIN", "VETERINARIO", "ESTILISTA")

                    .requestMatchers("/citas/mis-citas").authenticated()
                    .requestMatchers("/citas/**").hasAnyRole("RECEPCIONISTA", "ADMIN", "VETERINARIO", "ESTILISTA")

                    .requestMatchers("/facturas/mis-facturas").authenticated()
                    .requestMatchers(HttpMethod.POST, "/facturas/**").hasAnyRole("RECEPCIONISTA", "ADMIN")
                    .requestMatchers("/facturas/**").hasAnyRole("RECEPCIONISTA", "ADMIN")

                    .requestMatchers("/pagos/**").hasAnyRole("RECEPCIONISTA", "ADMIN")
                    .requestMatchers("/historial/**").hasAnyRole("VETERINARIO", "ESTILISTA", "ADMIN")
                    .requestMatchers("/calificaciones/**").authenticated()

                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
