# API de Reserva de Salas

## Qué es

API REST para crear salas de reunión y reservarlas por franjas horarias, con detección de solapes garantizada también en la base de datos. Incluye frontend Angular, autenticación JWT, migraciones Flyway, documentación OpenAPI y un stack completo en Docker.

**Stack**: Java 21 · Spring Boot 4.1.2 · PostgreSQL 17 · Flyway · JWT (resource server) · Testcontainers · Angular 22 · PrimeNG 22 · Docker Compose.

---

## Cómo ejecutarlo

La forma recomendada no necesita Java ni Node instalados:

```bash
cp .env.example .env
# edita .env y cambia JWT_SECRETO por una clave de al menos 32 caracteres
docker compose up --build
```

Servicios:

| Servicio | URL |
|---|---|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Frontend | http://localhost:8081 |

Credenciales del usuario semilla: **admin / admin123**. Al arrancar se siembran tres salas de ejemplo (Andes, Illimani y Titicaca) para que la aplicación no esté vacía.

### Desarrollo local

```bash
# 1. Base de datos
cp .env.example .env
docker compose up -d db

# 2. API (Java 21 y el wrapper de Maven)
cd rest-api-spring
export JWT_SECRETO="una-clave-local-de-al-menos-32-caracteres"
export DB_PUERTO=5432   # el puerto publicado por el compose
./mvnw spring-boot:run

# 3. Frontend (Node 22.22 o superior)
cd ../frontend
npm ci
npm start
```

El frontend queda en http://localhost:4200 y proxea `/api` al puerto 8080.

> Si tu máquina ya tiene un PostgreSQL ocupando el 5432, publica el contenedor en otro puerto con `DB_PUERTO=5433` en `.env` y exporta el mismo valor antes de arrancar la API.

---

## Cómo ejecutar los tests

```bash
cd rest-api-spring

./mvnw test     # 29 tests unitarios y de capa web, sin Docker
./mvnw verify   # añade 4 pruebas de integración con Testcontainers
```

Las pruebas de integración levantan PostgreSQL 17 en un contenedor, aplican las migraciones Flyway y ejercitan la API real: flujo completo de reserva, conflicto concurrente, login y rechazo de escrituras sin token.

---

## Referencia rápida de la API

| Método | Ruta | Auth |
|---|---|---|
| POST | `/api/v1/sesiones` | pública |
| POST | `/api/v1/salas` | JWT |
| GET | `/api/v1/salas?pagina&tamano` | pública |
| GET | `/api/v1/salas/{id}` | pública |
| POST | `/api/v1/reservas` | JWT |
| GET | `/api/v1/reservas?salaId&fecha&estado&pagina&tamano&orden` | pública |
| GET | `/api/v1/reservas/{id}` | pública |
| DELETE | `/api/v1/reservas/{id}` | JWT |

```bash
# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/sesiones \
  -H 'Content-Type: application/json' \
  -d '{"usuario":"admin","clave":"admin123"}' \
  | sed -E 's/.*"tokenAcceso":"([^"]+)".*/\1/')

# Crear una reserva
curl -s -X POST http://localhost:8080/api/v1/reservas \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{
        "salaId": "11111111-1111-4111-8111-111111111111",
        "nombreResponsable": "Ana Pérez",
        "correoResponsable": "ana@example.com",
        "inicio": "2026-11-20T10:00:00-04:00",
        "fin": "2026-11-20T11:00:00-04:00"
      }'

# Listar con filtros
curl -s "http://localhost:8080/api/v1/reservas?salaId=11111111-1111-4111-8111-111111111111&fecha=2026-11-20&pagina=0&tamano=10"
```

Los errores siguen RFC 9457 (Problem Details) con un campo `codigo` estable: `SALA_NO_DISPONIBLE`, `NOMBRE_SALA_OCUPADO`, `DATOS_INVALIDOS`, `NO_AUTENTICADO`, etc.

---

## Decisiones y trade-offs

- **Monolito modular con hexágono por módulo** → un solo proceso con fronteras `salas`, `reservas`, `seguridad` y `compartido`; se descartó microservicios porque el dominio no los justifica. Menos infraestructura y mejores pruebas.
- **Intervalo semiabierto `[inicio, fin)`** → dos reservas adyacentes (11:00–12:00 y 12:00–13:00) no chocan. Es lo que permite encadenar reuniones; la alternativa cerrada obligaría a dejar huecos artificiales.
- **Doble barrera contra el solape** → consulta en aplicación para dar un `409` claro y `EXCLUDE USING gist` en PostgreSQL para la garantía real. La primera sola es una promesa; la segunda sola devuelve errores técnicos.
- **Cancelación lógica** → la reserva se marca `CANCELADA` y deja de bloquear el horario; se descartó borrar la fila porque el histórico es información.
- **Filtro por fecha como día completo** → se interpreta en `America/La_Paz` y devuelve las reservas que intersectan ese día. La alternativa (“solo las que empiezan ese día”) es más simple pero oculta reservas que cruzan medianoche.
- **Problem Details con códigos propios** → el cliente decide por `codigo`, no por el texto. Se descartó devolver mensajes sueltos porque obliga a comparar cadenas.
- **JWT con `spring-boot-starter-oauth2-resource-server`** → el framework valida, firma y expone el token; se descartó escribir un filtro a mano. Menos código y menos errores de seguridad.
- **Paginación propia** → `contenido`, `pagina`, `tamano`, `totalElementos`, `totalPaginas` en lugar de serializar `Page` de Spring, cuyo formato ha cambiado entre versiones.
- **`409` al cancelar dos veces** → el cliente distingue “cancelé ahora” de “ya estaba cancelada”. La alternativa idempotente (`204` siempre) es más cómoda pero oculta el estado real.
- **Todo el proyecto en español** → paquetes, clases, tablas, rutas y documentación. Se pierde idiomaticidad para equipos internacionales, pero este entregable se lee en español de principio a fin.

---

## Qué haría con más tiempo

1. Edición de reservas revalidando el solape al mover el horario.
2. Autorización por responsable: que cada usuario solo cancele lo suyo.
3. Refresh tokens y revocación de sesiones.
4. Disponibilidad recurrente por sala (RRULE).
5. CI con Testcontainers en GitHub Actions.
6. Outbox y notificaciones al responsable.
7. ArchUnit o Spring Modulith como test de fronteras en el pipeline.
8. Métricas y trazas (Micrometer + OpenTelemetry).
9. Tests de frontend y caché de listados con invalidación por escritura.

---

## Cómo usé la IA

| Qué pedí | Qué acepté | Qué corregí o rechacé |
|---|---|---|
| Estructura base del repositorio | Pom de Spring Boot 4 del Initializr y compose de PostgreSQL | El generador traía el paquete `com.qabit.prueba_reuniones`, Lombok y faltaban Flyway, Actuator y springdoc; se reemplazó por `com.alain.reuniones` y se eliminó Lombok |
| Regla de solape y tests de fronteras | `RangoHorario` semiabierto con tabla de casos | Un test de doble cancelación falló con `NullPointerException`: la validación del instante tapaba el error de negocio; se reordenó y el test exige `ReservaYaCanceladaException` |
| Pruebas de integración con Testcontainers | Contenedor PostgreSQL 17 y `@ServiceConnection` | El contenedor se paraba entre clases y la segunda IT recibía `500`; se arrancó una sola vez por JVM. `TestRestTemplate` no resolvía en Boot 4 y se cambió a `RestClient` |
| Seguridad JWT | Resource server, BCrypt y Problem Details en el `401` | El token incluía el rol interno `FACTOR_PASSWORD`; se filtró el claim `roles` a las autoridades `ROLE_*` |
| Frontend Angular y PrimeNG | Componentes standalone, interceptor y guardia | PrimeNG 22 no registraba `MessageService` y su `p-table` había cambiado de API; se pasó a tabla HTML con `p-paginator` y botones dentro del diálogo |
| Documentación OpenAPI | Configuración con esquema bearer y anotaciones | Se evitó el requisito de seguridad global para no marcar como protegidas las lecturas públicas; se verificaron las 8 operaciones |

---

## Tiempo empleado

| Bloque | Tiempo |
|---|---|
| Arranque, configuración y documentación del proyecto | 1 h 05 min |
| Módulo de salas | 22 min |
| Reservas: dominio, persistencia, casos de uso e integración | 30 min |
| Seguridad JWT | 23 min |
| OpenAPI | 13 min |
| Frontend Angular + PrimeNG | 28 min |
| Docker, README y verificación del stack | 20 min |
| **Total** | **3 h 21 min** |

El tiempo incluye los ajustes y correcciones descritos arriba; no se redondea a tres horas.
