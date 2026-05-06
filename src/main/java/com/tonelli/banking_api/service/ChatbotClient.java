package com.tonelli.banking_api.service;

import com.tonelli.banking_api.dto.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

// ChatbotClient es el puente entre Java y Python
// Encapsula todas las llamadas HTTP al chatbot Python
// Si mañana cambia la URL o el protocolo, solo cambia esta clase
@Service
public class ChatbotClient {

    // URL del chatbot Python — hardcodeada para desarrollo local
    // En producción se configura via application.properties
    private final String chatbotUrl = "http://localhost:8000";

    // RestTemplate es el HTTP client de Spring
    // Equivalente a requests en Python
    private final RestTemplate restTemplate = new RestTemplate();

    // ── CREAR SESIÓN ───────────────────────────────────────────────────────────

    public String crearSesion() {
        // Llama a POST http://localhost:8000/sesion/nueva
        // El chatbot Python crea una sesión y devuelve el sesion_id
        Map response = restTemplate.postForObject(
                chatbotUrl + "/sesion/nueva",
                null,
                Map.class
        );
        return (String) response.get("sesion_id");
    }

    // ── ENVIAR MENSAJE ─────────────────────────────────────────────────────────

    public ChatResponse enviarMensaje(String sesionId, String mensaje) {
        // Armamos el body del request — igual que en el chatbot Python
        Map<String, String> request = Map.of(
                "sesion_id", sesionId,
                "mensaje", mensaje
        );

        // Llama a POST http://localhost:8000/chat
        // El chatbot Python procesa el mensaje y devuelve la respuesta
        Map response = restTemplate.postForObject(
                chatbotUrl + "/chat",
                request,
                Map.class
        );

        return new ChatResponse(
                sesionId,
                (String) response.get("respuesta"),
                (String) response.get("esperando")
        );
    }

    // ── CERRAR SESIÓN ──────────────────────────────────────────────────────────

    public void cerrarSesion(String sesionId) {
        // Llama a DELETE http://localhost:8000/sesion/{sesion_id}
        // Limpia el estado de la sesión en el chatbot Python
        restTemplate.delete(chatbotUrl + "/sesion/" + sesionId);
    }
}