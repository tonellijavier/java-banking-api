# Banking API — Java + Spring Boot + PostgreSQL

API REST bancaria construida con Spring Boot que expone operaciones de consulta y transferencia sobre una base de datos PostgreSQL real (Neon).

Comparte la misma base de datos que el [chatbot bancario en Python](https://github.com/tonellijavier/ai-engineer-portfolio/tree/main/chatbot-bancario) — demostrando integración entre dos sistemas con tecnologías distintas sobre los mismos datos, como ocurre en arquitecturas bancarias reales.

---

## Endpoints

```
GET  /api/clientes/{dni}/saldo       → saldo y productos del cliente
GET  /api/clientes/{dni}/movimientos → historial de movimientos ordenado por fecha
POST /api/transferencias             → ejecuta una transferencia a un contacto habilitado
```

### Ejemplo — Consultar saldo

```
GET /api/clientes/12345678/saldo

Response:
{
  "nombre": "Javier",
  "saldo": 84000.0,
  "productos": "Caja de ahorro en pesos, Tarjeta Visa débito"
}
```

### Ejemplo — Ejecutar transferencia

```
POST /api/transferencias
Body:
{
  "dniOrigen": "12345678",
  "cbuDestino": "0000003100025786490015",
  "monto": 1000
}

Response:
{
  "nombre": "Javier",
  "saldo": 83000.0,
  "productos": "..."
}
```

---

## Arquitectura

```
Request HTTP
      ↓
Controller   → recibe la request, valida el formato, devuelve la response
      ↓
Service      → lógica de negocio — validaciones, reglas, coordinación
      ↓
Repository   → acceso a la base de datos via JPA
      ↓
PostgreSQL (Neon)
```

**Patrón Controller → Service → Repository** — cada capa tiene una responsabilidad única y solo habla con la capa de abajo. Si mañana cambiás la base de datos, solo cambiás el Repository. Si cambiás el protocolo de comunicación, solo cambiás el Controller.

### Estructura del proyecto

```
src/main/java/com/tonelli/banking_api/
├── controller/
│   └── ClienteController.java      ← endpoints REST
├── service/
│   └── ClienteService.java         ← lógica de negocio
├── repository/
│   ├── ClienteRepository.java      ← acceso a tabla clientes
│   ├── ContactoRepository.java     ← acceso a tabla contactos
│   └── MovimientoRepository.java   ← acceso a tabla movimientos
├── model/
│   ├── Cliente.java                ← entidad JPA → tabla clientes
│   ├── Contacto.java               ← entidad JPA → tabla contactos
│   └── Movimiento.java             ← entidad JPA → tabla movimientos
├── dto/
│   ├── SaldoResponse.java          ← lo que devuelve GET /saldo
│   ├── MovimientoResponse.java     ← lo que devuelve GET /movimientos
│   └── TransferenciaRequest.java   ← lo que recibe POST /transferencias
└── BankingApiApplication.java      ← punto de entrada
```

---

## Decisiones de diseño

**DTOs separados de los Models**

Los Models representan las tablas de la base de datos con todos sus campos. Los DTOs exponen solo lo necesario al cliente — sin IDs internos ni datos sensibles. Cambiar la estructura interna no afecta lo que ve el usuario.

**@Transactional en transferencias**

La transferencia descuenta el saldo y registra el movimiento en dos operaciones SQL. `@Transactional` garantiza que si alguna falla, ambas se revierten — o todo o nada. Fundamental en operaciones financieras.

**Validación de contactos habilitados**

El endpoint de transferencia no acepta cualquier CBU — solo CBUs registrados en la tabla de contactos del cliente. Esto evita que un usuario inyecte un CBU arbitrario en el request.

**Base de datos compartida con el chatbot Python**

Ambos sistemas — la API Java y el chatbot bancario en Python — leen y escriben sobre la misma base de datos PostgreSQL. El saldo que actualiza la API Java es el mismo que ve el chatbot en la próxima consulta. Así funciona en producción real: múltiples sistemas sobre el mismo Core Banking.

---

## Base de datos — Neon (PostgreSQL)

```sql
clientes    → dni (PK), nombre, saldo, productos
contactos   → id, dni_cliente, nombre, cbu, alias
movimientos → id, dni_cliente, fecha, descripcion, monto
```

---

## Stack

- **Java 21** — versión LTS
- **Spring Boot 3.5** — framework principal
- **Spring Data JPA + Hibernate** — acceso a datos
- **Spring Web** — endpoints REST
- **Lombok** — elimina boilerplate (getters, setters, constructores)
- **PostgreSQL + Neon** — base de datos serverless compartida con el chatbot Python
- **Maven** — gestión de dependencias

---

## Setup

**Requisitos:**
- Java 21+
- Maven 3.9+

**Clonar y configurar:**

```bash
git clone https://github.com/tonellijavier/java-banking-api.git
cd java-banking-api
```

Copiás el archivo de ejemplo y configurás tus credenciales:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Editás `application.properties` con tus datos de conexión a PostgreSQL.

```properties
spring.datasource.url=jdbc:postgresql://host/neondb?sslmode=require
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
```

**Correr:**

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8081`