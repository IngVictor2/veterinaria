# Documentación Técnica — Sistema de Gestión Veterinaria

## 1. Resumen

API RESTful para la gestión integral de una clínica veterinaria construida con **Spring Boot 3 + Kotlin + SQLite in-memory + JWT**. Expone **69 endpoints** organizados en 17 controladores.

## 2. Stack Tecnológico

| Componente | Tecnología |
|------------|-----------|
| Lenguaje | Kotlin 2.2 |
| Framework | Spring Boot 3.5 |
| Base de datos | SQLite 3 (in-memory con `cache=shared`) |
| ORM | Hibernate 6 + Spring Data JPA |
| Autenticación | JWT (HMAC-SHA, 30 min expiración) |
| Documentación | SpringDoc OpenAPI + Swagger UI |
| Build | Gradle 9.5 |
| Testing | Spring Boot Test + JUnit 5 |

## 3. Arquitectura

```
┌──────────────────────────────────────────────────────────┐
│                    Controladores (17)                     │
│  Auth  │  Cliente  │  Mascota  │  Cita  │  Factura      │
│  Pago  │  Servicio │ Empleado  │ Rol    │  Permisos      │
│  Calificacion  │  Historial  │  Usuario-Rol   │  Cargo   │
│  MetodoPago  │  Modulo  │  Health                       │
└──────────────────────┬───────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                   Servicios                          │
│  AuthService  │  ClienteService  │  CitaService      │
│  FacturaService  │  PagoService  │  ...              │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                 Repositorios (JPA)                   │
│  20 interfaces extendiendo JpaRepository             │
└──────────────────────┬──────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────┐
│                   Entidades (22)                     │
│  Cliente │ Mascota │ Cita │ Factura │ Pago │ Usuario │
│  Rol │ Modulo │ Servicio │ Historial │ ...           │
└─────────────────────────────────────────────────────┘
```

### Capa de Seguridad

```
Solicitud → JwtAuthenticationFilter → SecurityFilterChain
               │                              │
               ▼                              ▼
         Valida JWT                      Valida URL + Rol
         Extrae claims                   (hasRole/hasAnyRole)
               │                              │
               ▼                              ▼
      SecurityContextHolder           Acceso permitido/denegado
      (principal = idUsuario)         (401/403 vía handlers JSON)
```

## 4. Base de Datos

### Esquema Relacional (22 tablas)

```
cliente ──< mascota ──< cita >── empleado
  │                    │   │
  │                    │   └── servicio
  │                    │
  └──< factura ──< detalle_factura >── cita
        │                         └── servicio
        │
        └──< pago >── metodo_pago

usuario >── usuario_rol >── rol >── rol_modulo >── modulo
  │
  ├── cliente (1:1 opcional)
  └── empleado (1:1 opcional)

cita >── consulta_medica (1:1 opcional)
cita >── servicio_estetica (1:1 opcional)

historial_mascota >── mascota
                  >── cita
                  >── consulta_medica (opcional)
                  >── servicio_estetica (opcional)

password_reset_token >── usuario
```

### Entidades y Enums

| Entidad | PK | Relaciones Clave |
|---------|----|-------------------|
| `Cliente` | id_cliente | → Mascota (1:N), → Factura (1:N), → Usuario (1:1) |
| `Mascota` | id_mascota | → Cliente (N:1), → Cita (1:N), → Historial (1:N) |
| `Empleado` | id_empleado | → Cargo (N:1), → Usuario (1:1), → Cita (1:N) |
| `Cita` | id_cita | → Mascota (N:1), → Empleado (N:1), → Servicio (N:1), → ConsultaMedica (1:1), → ServicioEstetica (1:1) |
| `Factura` | id_factura | → Cliente (N:1), → DetalleFactura (1:N), → Pago (1:N) |
| `Usuario` | id_usuario | → Cliente (1:1 opt), → Empleado (1:1 opt), → UsuarioRol (1:N) |
| `Rol` | id_rol | → RolModulo (1:N), → UsuarioRol (1:N) |
| `Modulo` | id_modulo | → RolModulo (1:N) |

| Enum | Valores |
|------|---------|
| `TipoDocumento` | CC, CE, TI, PASAPORTE |
| `TipoServicio` | CONSULTA, ESTETICA, OTRO |
| `SexoMascota` | MACHO, HEMBRA |
| `EstadoCita` | PENDIENTE, CONFIRMADA, ATENDIDA, CANCELADA |
| `EstadoFactura` | PENDIENTE, PAGADA, ANULADA |
| `TipoHistorial` | MEDICO, ESTETICA |

### Máquina de Estados — Cita

```
PENDIENTE ──→ CONFIRMADA ──→ ATENDIDA
     │              │
     └──→ CANCELADA └──→ CANCELADA
```

## 5. API — Endpoints

### Health (`/`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/` | ❌ | Health check de la API |

### Autenticación (`/auth`)

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| POST | `/auth/register` | ❌ | Registro de cliente |
| POST | `/auth/login` | ❌ | Inicio de sesión → JWT |
| POST | `/auth/logout` | ✅ | Mensaje informativo |
| POST | `/auth/cambiar-password` | ✅ | Cambiar contraseña |
| POST | `/auth/recuperar-password` | ❌ | Solicitar token de recuperación |
| POST | `/auth/reset-password?token=` | ❌ | Resetear contraseña con token |

### Clientes (`/clientes`)

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/clientes` | RECEP, ADMIN | Listar todos |
| GET | `/clientes/me` | Authenticated | Perfil del cliente autenticado |
| PUT | `/clientes/me` | Authenticated | Actualizar perfil propio (nombre, teléfono, correo, dirección) |
| GET | `/clientes/{id}` | RECEP, ADMIN | Buscar por ID |
| POST | `/clientes/{id}/usuario` | RECEP, ADMIN | Crear usuario para cliente existente |
| POST | `/clientes` | RECEP, ADMIN | Crear |
| PUT | `/clientes/{id}` | RECEP, ADMIN | Actualizar |
| DELETE | `/clientes/{id}` | RECEP, ADMIN | Eliminar (soft) |

### Mascotas (`/mascotas`)

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/mascotas` | RECEP, ADMIN, VET, EST | Listar todas |
| GET | `/mascotas/mis-mascotas` | Authenticated | Mis mascotas |
| GET | `/mascotas/{id}` | RECEP, ADMIN, VET, EST | Buscar por ID |
| GET | `/mascotas/cliente/{idCliente}` | RECEP, ADMIN, VET, EST | Listar por cliente |
| POST | `/mascotas` | RECEP, ADMIN, VET, EST | Crear |
| PUT | `/mascotas/{id}` | RECEP, ADMIN, VET, EST | Actualizar |
| DELETE | `/mascotas/{id}` | RECEP, ADMIN, VET, EST | Eliminar (soft) |

### Citas (`/citas`)

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/citas` | RECEP, ADMIN, VET, EST | Listar todas |
| GET | `/citas/mis-citas` | Authenticated | Mis citas |
| GET | `/citas/{id}` | RECEP, ADMIN, VET, EST | Buscar por ID |
| GET | `/citas/cliente/{idCliente}` | RECEP, ADMIN, VET, EST | Por cliente |
| GET | `/citas/mascota/{idMascota}` | RECEP, ADMIN, VET, EST | Por mascota |
| GET | `/citas/empleado/{idEmpleado}` | RECEP, ADMIN, VET, EST | Por empleado |
| POST | `/citas` | RECEP, ADMIN, VET, EST | Crear |
| PUT | `/citas/{id}` | RECEP, ADMIN, VET, EST | Reprogramar |
| PATCH | `/citas/{id}/estado` | RECEP, ADMIN, VET, EST | Cambiar estado |
| POST | `/citas/{id}/cancelar` | RECEP, ADMIN, VET, EST | Cancelar |
| POST | `/citas/consulta` | RECEP, ADMIN, VET, EST | Registrar consulta médica |
| POST | `/citas/estetica` | RECEP, ADMIN, VET, EST | Registrar servicio estético |

### Facturas (`/facturas`)

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/facturas` | RECEP, ADMIN | Listar todas |
| GET | `/facturas/mis-facturas` | Authenticated | Mis facturas |
| GET | `/facturas/{id}` | RECEP, ADMIN | Buscar por ID |
| GET | `/facturas/cliente/{idCliente}` | RECEP, ADMIN | Por cliente |
| POST | `/facturas` | RECEP, ADMIN | Crear |
| PATCH | `/facturas/{id}/estado` | RECEP, ADMIN | Cambiar estado (PENDIENTE→PAGADA/ANULADA) |

### Pagos (`/pagos`) — RECEPCIONISTA, ADMIN

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/pagos` | Listar todos los pagos |
| GET | `/pagos/factura/{idFactura}` | Listar pagos de factura |
| POST | `/pagos` | Registrar pago |

### Servicios (`/servicios`)

| Método | Ruta | Rol | Descripción |
|--------|------|-----|-------------|
| GET | `/servicios` | ❌ (público) | Listar (filtro opcional `?tipo=`) |
| GET | `/servicios/{id}` | ❌ (público) | Buscar por ID |
| POST | `/servicios` | ADMIN | Crear |
| PUT | `/servicios/{id}` | ADMIN | Actualizar |
| DELETE | `/servicios/{id}` | ADMIN | Eliminar (soft) |

### Empleados (`/empleados`) — ADMIN

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/empleados` | Listar todos |
| GET | `/empleados/{id}` | Buscar por ID |
| POST | `/empleados` | Crear (con usuario) |
| PUT | `/empleados/{id}` | Actualizar |
| DELETE | `/empleados/{id}` | Eliminar (soft) |

### Catálogos — Referencias

| Endpoint | Rol | Descripción |
|----------|-----|-------------|
| `GET /cargos` | Authenticated | Lista cargos (VETERINARIO, ESTILISTA, etc.) |
| `GET /metodos-pago` | Authenticated | Lista métodos de pago (EFECTIVO, TARJETA, TRANSFERENCIA) |
| `GET /modulos` | Authenticated | Lista módulos de permiso (CLIENTES, CITAS, etc.) |

### Calificaciones (`/calificaciones`) — Authenticated

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/calificaciones/cliente/{idCliente}` | Calificaciones por cliente |
| POST | `/calificaciones` | Calificar cita atendida |

### Historial (`/historial`) — VETERINARIO, ESTILISTA, ADMIN

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/historial/mascota/{idMascota}` | Historial clínico por mascota |

### Admin — Roles (`/admin/roles`) — ADMIN

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/admin/roles` | Listar roles |
| POST | `/admin/roles` | Crear rol |
| PUT | `/admin/roles/{id}` | Actualizar rol |
| DELETE | `/admin/roles/{id}` | Eliminar (soft) |

### Admin — Permisos (`/admin/permisos`) — ADMIN

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/admin/permisos/rol/{idRol}` | Permisos por rol |
| POST | `/admin/permisos` | Asignar módulo a rol |
| DELETE | `/admin/permisos/{idRol}/{idModulo}` | Revocar permiso |

### Admin — Usuario-Roles (`/admin/usuario-roles`) — ADMIN

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/admin/usuario-roles/usuario/{idUsuario}` | Roles de usuario |
| POST | `/admin/usuario-roles` | Asignar rol a usuario |
| DELETE | `/admin/usuario-roles/{idUsuario}/{idRol}` | Revocar rol |

## 6. Seguridad

### JWT

- **Header**: `Authorization: Bearer <token>`
- **Payload**: `sub` = ID usuario (Int), `nombreUsuario`, `roles` (List<String>)
- **Expiración**: 30 minutos (configurable vía `app.jwt.expiration-minutes`)
- **Firma**: HMAC-SHA (clave configurable vía `app.jwt.secret`)
- **Password encoding**: BCrypt

### Roles del sistema

| Rol | Acceso principal |
|-----|------------------|
| `ADMIN` | Todo el sistema |
| `VETERINARIO` | Mascotas, Citas, Historial |
| `ESTILISTA` | Mascotas, Citas, Historial |
| `RECEPCIONISTA` | Clientes, Mascotas, Citas, Facturas, Pagos |
| `CLIENTE` | Mis mascotas, mis citas, mis facturas, calificaciones |

### Jerarquía de excepciones

Todas las excepciones de dominio extienden `ApiException`, que lleva asociado el `HttpStatus` correspondiente:

```
ApiException (message, status)
├── ResourceNotFoundException          → 404
├── DuplicateResourceException         → 409
├── InvalidCredentialsException        → 401
├── InvalidStatusTransitionException   → 400
└── InvalidRequestException            → 400
```

El `GlobalExceptionHandler` captura `ApiException` con un único handler genérico y mantiene handlers separados solo para excepciones de Spring (`BadCredentialsException`, `AccessDeniedException`, validación, etc.).

### Manejo de errores

| Código | Excepción | Causa |
|--------|-----------|-------|
| 400 | `InvalidRequestException`, `InvalidStatusTransitionException` | Validación, argumento inválido, transición de estado no permitida |
| 401 | `InvalidCredentialsException`, `BadCredentialsException` | Credenciales inválidas / token ausente |
| 403 | `AccessDeniedException` | Sin permisos para el recurso |
| 404 | `ResourceNotFoundException` | Recurso no encontrado |
| 409 | `DuplicateResourceException`, `DataIntegrityViolationException` | Duplicado / violación de integridad |
| 500 | `Exception` (genérica) | Error interno no controlado |

```json
{
  "timestamp": "2026-07-20T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Cliente no encontrado",
  "path": "/clientes/999"
}
```

## 7. Configuración

### `application.yml` (principales)

```yaml
spring:
  datasource:
    url: jdbc:sqlite:file:vet_server_kotlin?mode=memory&cache=shared
  jpa:
    hibernate:
      ddl-auto: create-drop
    open-in-view: true

app:
  jwt:
    secret: ${JWT_SECRET:this-is-a-dev-secret-must-be-at-least-32-bytes}
    expiration-minutes: ${JWT_EXPIRATION_MINUTES:30}
```

### Datos iniciales (seed)

Al iniciar, `DataInitializer` crea:

- **1 admin**: username=`admin`, password=`admin123`, correo=`admin@veterinaria.com`
- **5 roles**: ADMIN, VETERINARIO, ESTILISTA, RECEPCIONISTA, CLIENTE
- **4 cargos**: VETERINARIO, ESTILISTA, RECEPCIONISTA, ADMINISTRADOR
- **7 servicios**: Consulta General ($50k), Consulta Especializada ($80k), Vacunación ($35k), Baño Medicado ($40k), Corte de Pelo ($35k), Limpieza Dental ($60k), Cirugía Menor ($150k)
- **4 métodos de pago**: EFECTIVO, TARJETA_DEBITO, TARJETA_CREDITO, TRANSFERENCIA
- **9 módulos de permiso**: CLIENTES, MASCOTAS, CITAS, FACTURACION, HISTORIAL, USUARIOS, ROLES, TARIFAS, CALIFICACIONES

## 8. Instalación y Ejecución

### Requisitos

- JDK 17+
- Gradle 9.x (o usar el wrapper)

### Ejecutar

```bash
# Desarrollo
gradle bootRun

# Build
gradle build

# Tests
gradle test
```

### Acceso

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 9. Estructura del Proyecto

```
src/main/kotlin/co/edu/iub/veterinaria/
├── VeterinariaApplication.kt
├── config/
│   ├── DataInitializer.kt      # Seed data
│   ├── JpaConfig.kt             # Auditoría JPA
│   ├── OpenApiConfig.kt         # Swagger config
│   └── SecurityConfig.kt        # Seguridad HTTP
├── controller/ (17)             # REST endpoints
├── dto/
│   ├── admin/ (6)               # Rol, permiso DTOs
│   ├── auth/ (6)                # Auth DTOs
│   ├── calificacion/ (2)
│   ├── cita/ (4)
│   ├── cliente/ (4)               # + ClienteProfileRequest, CrearUsuarioClienteRequest
│   ├── empleado/ (2)
│   ├── error/ (1)
│   ├── factura/ (2)
│   ├── historial/ (1)
│   ├── mascota/ (2)
│   ├── pago/ (2)
│   └── servicio/ (2)
├── exception/
│   ├── DuplicateResourceException.kt
│   ├── GlobalExceptionHandler.kt  # Manejador global (@RestControllerAdvice)
│   └── ResourceNotFoundException.kt
├── model/ (22)                  # Entidades JPA + enums
├── repository/ (20)             # Repositorios JPA
├── security/
│   ├── CurrentUserHelper.kt
│   ├── CustomUserDetailsService.kt
│   ├── JwtAuthenticationFilter.kt
│   ├── JwtTokenProvider.kt
│   └── SecurityHandlers.kt
├── service/ (9)                 # Lógica de negocio
└── util/                        # (vacío)
```

## 10. Licencia

Proyecto académico — IUB.
