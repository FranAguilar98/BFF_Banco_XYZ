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

```bash
# 1) Levantar Postgres
docker compose up -d

# 2) Compilar todo el multi-módulo desde la raíz de este proyecto
mvn clean install

# 3) Levantar los microservicios primero (los BFF dependen de ellos)
mvn -pl ms-cuentas       spring-boot:run
mvn -pl ms-movimientos   spring-boot:run
mvn -pl ms-transacciones spring-boot:run

# 4) Levantar cada BFF en una terminal distinta
mvn -pl bff-web    spring-boot:run
mvn -pl bff-mobile spring-boot:run
mvn -pl bff-atm    spring-boot:run
```

Por defecto, los tres microservicios esperan Postgres en
`localhost:5432`, base `bank_batch`, usuario/clave
`bank_batch`/`bank_batch` (mismos valores que el `docker-compose.yml`).
Los BFF, a su vez, esperan a los microservicios en `localhost:8090`
(`ms-cuentas`), `localhost:8091` (`ms-movimientos`) y `localhost:8092`
(`ms-transacciones`). Todo es configurable por variables de entorno
(`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`,
`SERVER_PORT`, etc.), ver cada `application.yml`.

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