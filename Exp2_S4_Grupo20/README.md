# Banco XYZ — Suite BFF (Backend for Frontend)

**PBY2203 — Desarrollo Backend III — Semana 4 y 5 (Exp2)**

## 1. Objetivo del proyecto

Implementar el patrón **Backend for Frontend (BFF)** para optimizar la
comunicación entre los distintos frontends del Banco XYZ, integrando y
agregando información desde servicios backend independientes. Se
implementa **un BFF independiente por canal**, y **un microservicio
independiente por dominio de datos**:

| BFF | Puerto | Cliente | Enfoque |
|---|---|---|---|
| `bff-web` | 8081 | Navegador / back-office | Datos completos, paginación y filtros |
| `bff-mobile` | 8082 | App móvil | Respuestas livianas, mínimo consumo de ancho de banda |
| `bff-atm` | 8083 | Cajero automático | Superficie mínima, máxima seguridad, operaciones críticas |

| Microservicio | Puerto | Dueño de |
|---|---|---|
| `ms-cuentas` | 8090 | Saldo e interés de cada cuenta |
| `ms-movimientos` | 8091 | Historial de retiros/depósitos por cuenta |
| `ms-transacciones` | 8092 | Feed global de transacciones (detección de anomalías) |
| `config-server` | 8888 | Configuración centralizada (Spring Cloud Config) |
| `eureka-server` | 8761 | Service Discovery (Spring Cloud Netflix Eureka) |

Ningún BFF accede a la base de datos directamente: todos llaman a estos
tres microservicios por HTTP (con `RestClient`), y combinan su
información según lo que necesita cada canal — por ejemplo, un retiro en
`bff-atm` llama a `ms-cuentas` para descontar el saldo **y** a
`ms-movimientos` para registrar el movimiento correspondiente.

El análisis completo de las decisiones de arquitectura (BFF como
servicios independientes, y microservicios de dominio detrás de ellos)
está en [`docs/ADR-01-estrategia-bff.md`](docs/ADR-01-estrategia-bff.md).


## 2. Estructura del código
Exp2_S4_Grupo20/
├── pom.xml # POM padre (multi-módulo Maven)
├── docker-compose.yml # Postgres compartido
├── docs/
│ └── ADR-01-estrategia-bff.md
├── evidencia/ # Capturas / salidas de consola (ver sección 6)
├── bff-core/ # Librería compartida: solo JWT/seguridad
│ └── .../core/
│ ├── security/ # JwtTokenProvider, JwtAuthenticationFilter
│ └── exception/ # Excepciones de dominio compartidas
├── bff-web/ # Spring Boot app — canal Web (puerto 8081)
├── bff-mobile/ # Spring Boot app — canal Móvil (puerto 8082)
├── bff-atm/ # Spring Boot app — canal Cajero (puerto 8083)
├── ms-cuentas/ # Microservicio — cuentas/saldo (puerto 8090)
├── ms-movimientos/ # Microservicio — historial de movimientos (puerto 8091)
└── ms-transacciones/ # Microservicio — feed global de transacciones (puerto 8092)

Cada `bff-*` sigue la misma forma interna: `config` (seguridad + clientes
HTTP), `client` (llamadas a los microservicios), `controller`, `service`,
`dto`. Cada `ms-*` sigue la misma forma: `entity`, `repository`,
`controller`, `dto` — son los únicos módulos que tienen acceso JPA a la
base de datos.

## 3. Requisitos previos

- Java 17
- Maven 3.9+
- Docker (para levantar PostgreSQL) — o un PostgreSQL local en `localhost:5432`

## 4. Cómo ejecutar

### Modo de prueba (H2 en memoria — el usado para la evidencia de este entregable)

```bash
# 1) Compilar e instalar todo el multi-módulo (una sola vez, o cada vez que cambie el código)
mvn clean install

# 2) Levantar primero la infraestructura de Spring Cloud, cada uno en su propia terminal
mvn -pl config-server spring-boot:run
mvn -pl eureka-server spring-boot:run

# 3) Levantar los microservicios (ms-cuentas depende de config-server; ms-movimientos se registra en eureka-server)
mvn -pl ms-cuentas       spring-boot:run
mvn -pl ms-movimientos   spring-boot:run
mvn -pl ms-transacciones spring-boot:run

# 4) Levantar cada BFF, cada uno en su propia terminal
mvn -pl bff-web    spring-boot:run
mvn -pl bff-mobile spring-boot:run
mvn -pl bff-atm    spring-boot:run
```

Cada microservicio imprime en su log cuántos registros cargó y cuántos
rechazó por datos inválidos (por ejemplo:
`ms-cuentas: 17 cuentas cargadas, 983 filas rechazadas por datos invalidos`),
ya que el dataset oficial incluye intencionalmente filas con datos
inconsistentes.

Cuentas de ejemplo ya cargadas y listas para probar: `101, 105, 106, 108,
109, 117, 118, 122, 124, 127, 128, 130, 132, 133, 143, 144, 147`.

Puedes inspeccionar los datos cargados desde el navegador en la consola
de H2 de cada microservicio, por ejemplo:
`http://localhost:8090/h2-console` (JDBC URL: `jdbc:h2:mem:mscuentas`,
usuario `sa`, sin contraseña).

Puedes verificar el panel de Eureka en `http://localhost:8761`, y la
configuración servida por el Config Server en
`http://localhost:8888/ms-cuentas/default`.

### Modo productivo (PostgreSQL vía Docker)

Para un despliegue más cercano a producción, cada microservicio también
puede apuntar a PostgreSQL en vez de H2:

```bash
docker compose up -d
```

Esto levanta un Postgres en `localhost:5432`, base `bank_batch`,
usuario/clave `bank_batch`/`bank_batch`. En este modo, los datos deben
poblarse mediante el proyecto de migración batch de la Semana 3, no con
los cargadores CSV.

## 5. Autenticación y autorización por canal

Cada BFF firma y valida **su propio JWT** (clave/issuer distintos), de
forma que un token de un canal no sirve en otro.

### BFF Web (`/api/web/**`)
POST /api/web/auth/login
{ "usuario": "admin.web", "password": "Admin#2026" }
Usuarios demo: `admin.web`/`Admin#2026` (rol `WEB_ADMIN`+`WEB_USER`),
`operador.web`/`Oper#2026` (rol `WEB_USER`). Token válido 60 min.

### BFF Móvil (`/api/mobile/**`)
POST /api/mobile/auth/login
{ "usuario": "cliente.app", "password": "Cliente#2026" }
Token válido solo 15 min (política más estricta por ser dispositivo
personal).

### BFF Cajero (`/api/atm/**`)
Requiere **dos factores de acceso**:
1. Header `X-Atm-Device-Key: atm-device-key-demo-cambiar` en **toda**
   petición (identifica al cajero físico como dispositivo autorizado).
2. Login con tarjeta + PIN, token válido solo 3 minutos:
POST /api/atm/auth/login
Header: X-Atm-Device-Key: atm-device-key-demo-cambiar
Body: { "numeroTarjeta": "4551000000000001", "pin": "1234" }
Tarjetas demo: `4551000000000001`/PIN `1234` → cuenta 1001;
`4551000000000002`/PIN `5678` → cuenta 1002. El token queda ligado a esa
cuenta: no se puede usar para consultar/retirar de otra (ver
`AtmAccessGuard`).

Los tres microservicios (`ms-cuentas`, `ms-movimientos`,
`ms-transacciones`) no exponen autenticación propia — son servicios
internos, solo alcanzables por los BFF dentro de la misma red.

## 6. Endpoints por BFF

### Web
- `GET /api/web/cuentas?tipo=&page=&size=` — listado paginado, datos completos
- `GET /api/web/cuentas/{cuentaOrigenId}` — detalle completo de una cuenta
- `GET /api/web/cuentas/{cuentaOrigenId}/historial-anual` — historial anual
- `GET /api/web/transacciones?desde=&hasta=&tipo=&page=&size=` — feed global paginado

### Móvil
- `GET /api/mobile/cuentas/{cuentaOrigenId}/resumen` — `{cuentaOrigenId, nombre, tipo, saldo}`
- `GET /api/mobile/movimientos/recientes` — últimos 10 movimientos, campos mínimos

### Cajero
- `GET /api/atm/cuentas/{cuentaOrigenId}/saldo` — solo saldo disponible
- `POST /api/atm/cuentas/{cuentaOrigenId}/retiro` `{ "monto": 50000 }` — retiro con validación de saldo; internamente descuenta el saldo en `ms-cuentas` y registra el movimiento en `ms-movimientos`

### Microservicios (uso interno de los BFF)
- `ms-cuentas`: `GET /cuentas`, `GET /cuentas/{id}`, `PATCH /cuentas/{id}/saldo`
- `ms-movimientos`: `GET /movimientos/cuenta/{id}`, `POST /movimientos`
- `ms-transacciones`: `GET /transacciones`, `GET /transacciones/recientes`

Todas las respuestas de error siguen el mismo formato
`{ "timestamp", "status", "error" }`.

## 7. Spring Cloud (Semana 6): configuración centralizada, service discovery y tolerancia a fallos

### Config Server (`config-server`, puerto 8888)

Externaliza la configuración de `ms-cuentas`, que dejó de tener su propio
`application.yml` completo y en su lugar le pregunta a este servidor al
arrancar (`spring.config.import: optional:configserver:http://localhost:8888`).
Modo `native`: la configuración vive en
`config-server/src/main/resources/config-repo/`, como archivos locales
(sin depender de un repositorio Git externo).

Verificación: `GET http://localhost:8888/ms-cuentas/default` devuelve el
`ms-cuentas.yml` servido; el log de arranque de `ms-cuentas` confirma
`Fetching config from server at : http://localhost:8888`.

### Service Discovery (`eureka-server`, puerto 8761)

`ms-movimientos` se registra automáticamente en Eureka al arrancar
(`eureka.client.service-url.defaultZone: http://localhost:8761/eureka/`),
de forma que puede ser localizado por nombre en vez de por URL fija.

Verificación: el panel `http://localhost:8761` muestra
`MS-MOVIMIENTOS` con estado `UP` en la tabla de instancias registradas;
el log de arranque confirma `Registering application MS-MOVIMIENTOS with
eureka with status UP`.

### Tolerancia a fallos (`bff-atm`, con Resilience4j)

`bff-atm` protege sus llamadas salientes a `ms-cuentas` (consulta de
saldo y retiro) con un circuit breaker (`@CircuitBreaker`) y reintentos
(`@Retry`), configurados en `bff-atm/src/main/resources/application.yml`:

- Ventana de 5 llamadas, mínimo 3 para evaluar; si el 50% o más falla,
  el circuito se abre por 10 segundos.
- Hasta 3 reintentos con 500ms de espera entre cada uno.
- Si el circuito está abierto o los reintentos se agotan, se activa un
  método de fallback que devuelve un error explícito ("servicio no
  disponible") en vez de datos inventados o dejar la petición esperando
  indefinidamente.

Verificación: con `ms-cuentas` detenido, las peticiones a
`GET /api/atm/cuentas/{id}/saldo` fallan de forma controlada (en vez de
quedarse esperando), y `GET http://localhost:8083/actuator/health`
muestra el estado del circuito (`circuitBreakers.msCuentas.state`).