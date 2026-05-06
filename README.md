# Banking API — Java + Spring Boot + PostgreSQL

API REST bancaria construida con Spring Boot que expone operaciones de consulta, transferencia y chat conversacional sobre una base de datos PostgreSQL real (Neon).

Comparte la misma base de datos que el [chatbot bancario en Python](https://github.com/tonellijavier/ai-engineer-portfolio/tree/main/chatbot-bancario) — demostrando integración real entre dos sistemas con tecnologías distintas sobre los mismos datos, como ocurre en arquitecturas bancarias reales.

---

## Endpoints

```
GET  /api/clientes/{dni}/saldo       → saldo y productos del cliente
GET  /api/clientes/{dni}/movimientos → historial de movimientos ordenado por fecha
POST /api/transferencias             → ejecuta una transferencia a un contacto habilitado
POST /api/chat/sesion                → crea una sesión de chat con el chatbot Python
POST /api/chat/mensaje               → envía un mensaje al chatbot y devuelve la respuesta
DELETE /api/chat/sesion/{sesionId}   → cierra la sesión de chat
```

---

## Arquitectura

```
Cliente (app / frontend)
        ↓
API Java — Spring Boot :8081
        ├── Valida DNI en PostgreSQL
        ├── Ejecuta transferencias con @Transactional
        └── Llama al chatbot Python via HTTP
                ↓
        Chatbot Python — FastAPI :8000
                ├── LangGraph — maneja el flujo conversacional
                ├── Groq (Llama 3.3) — genera las respuestas
                └── PostgreSQL (Neon) — estado de sesiones
                        ↓
                PostgreSQL (Neon) — base de datos compartida
```

Java actúa como **gateway** — autenticación, validación de negocio, operaciones financieras. Python maneja toda la lógica de AI. Cada sistema hace lo que mejor sabe hacer.

---

## Decisiones de diseño

**Gateway pattern**

Java no llama al LLM directamente — delega al chatbot Python via HTTP. Si mañana cambiás de Groq a Azure OpenAI, solo cambiás el servicio Python. Java no se entera.

**Base de datos compartida**

Ambos sistemas leen y escriben sobre la misma base de datos PostgreSQL. El saldo que actualiza la API Java es el mismo que consulta el chatbot Python en la próxima conversación. Así funciona en producción real: múltiples sistemas sobre el mismo Core Banking.

**@Transactional en transferencias**

La transferencia descuenta el saldo y registra el movimiento en dos operaciones SQL. `@Transactional` garantiza que si alguna falla, ambas se revierten — o todo o nada.

**Validación de contactos habilitados**

El endpoint de transferencia solo acepta CBUs registrados en la tabla de contactos del cliente — no cualquier CBU arbitrario. Refleja cómo funcionan los bancos reales.

**Patrón Controller → Service → Repository**

Cada capa tiene una responsabilidad única. Si cambiás la base de datos, solo cambiás el Repository. Si cambiás el protocolo, solo cambiás el Controller.

---

## Estructura del proyecto

```
src/main/java/com/tonelli/banking_api/
├── controller/
│   ├── ClienteController.java      ← endpoints de saldo, movimientos y transferencias
│   └── ChatController.java         ← endpoints de chat — delega al chatbot Python
├── service/
│   ├── ClienteService.java         ← lógica de negocio bancario
│   └── ChatbotClient.java          ← HTTP client que habla con el chatbot Python
├── repository/
│   ├── ClienteRepository.java
│   ├── ContactoRepository.java
│   └── MovimientoRepository.java
├── model/
│   ├── Cliente.java
│   ├── Contacto.java
│   └── Movimiento.java
├── dto/
│   ├── SaldoResponse.java
│   ├── MovimientoResponse.java
│   ├── TransferenciaRequest.java
│   ├── ChatRequest.java            ← lo que recibe POST /api/chat/mensaje
│   └── ChatResponse.java           ← lo que devuelve el chatbot
└── GlobalExceptionHandler.java     ← manejo de errores — 404, 400 con mensajes claros
```

---

## Manejo de errores

```
404 → cliente no encontrado, contacto no habilitado
400 → saldo insuficiente, monto inválido
500 → error inesperado
```

Todos los errores devuelven JSON con timestamp, status, y mensaje claro — no páginas de error genéricas.

---

## Stack

- **Java 21** — versión LTS
- **Spring Boot 3.5** — framework principal
- **Spring Data JPA + Hibernate** — acceso a datos
- **Spring Web + RestTemplate** — endpoints REST y HTTP client
- **Lombok** — elimina boilerplate
- **PostgreSQL + Neon** — base de datos serverless compartida con el chatbot Python
- **Maven** — gestión de dependencias

---

## Setup

**Requisitos:** Java 21+, Maven 3.9+

```bash
git clone https://github.com/tonellijavier/java-banking-api.git
cd java-banking-api
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Editás `application.properties` con tus credenciales de PostgreSQL y corrés:

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8081`.

Para la integración con el chatbot Python, el chatbot tiene que estar corriendo en `http://localhost:8000`.