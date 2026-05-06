package com.tonelli.banking_api.controller;

import com.tonelli.banking_api.dto.ChatRequest;
import com.tonelli.banking_api.dto.ChatResponse;
import com.tonelli.banking_api.repository.ClienteRepository;
import com.tonelli.banking_api.service.ChatbotClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// ChatController expone los endpoints de chat al mundo exterior
// Valida que el cliente existe antes de hablar con el chatbot Python
// Java actúa como gateway — autenticación y validación aquí,
// lógica de AI en el chatbot Python
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatbotClient chatbotClient;
    private final ClienteRepository clienteRepository;

    public ChatController(ChatbotClient chatbotClient,
                          ClienteRepository clienteRepository) {
        this.chatbotClient = chatbotClient;
        this.clienteRepository = clienteRepository;
    }

    // ── POST /api/chat/sesion ──────────────────────────────────────────────────
    //
    // Crea una nueva sesión de chat para un cliente
    // Valida que el cliente existe antes de crear la sesión
    //
    // Body: {"dni": "12345678"}
    // Response: {"sesionId": "abc123"}
    @PostMapping("/sesion")
    public ResponseEntity<Map<String, String>> crearSesion(@RequestBody Map<String, String> body) {
        String dni = body.get("dni");

        // Validamos que el cliente existe — no cualquiera puede abrir una sesión
        clienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dni));

        // Delegamos la creación de sesión al chatbot Python
        String sesionId = chatbotClient.crearSesion();

        return ResponseEntity.ok(Map.of("sesionId", sesionId));
    }

    // ── POST /api/chat/mensaje ─────────────────────────────────────────────────
    //
    // Envía un mensaje al chatbot y devuelve la respuesta
    //
    // Body: {"dni": "12345678", "sesionId": "abc123", "mensaje": "cuánto tengo?"}
    // Response: {"sesionId": "abc123", "respuesta": "Tu saldo es...", "esperando": ""}
    @PostMapping("/mensaje")
    public ResponseEntity<ChatResponse> enviarMensaje(@RequestBody ChatRequest request) {

        // Validamos que el cliente existe
        clienteRepository.findByDni(request.getDni())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + request.getDni()));

        // Enviamos el mensaje al chatbot Python y devolvemos la respuesta
        ChatResponse response = chatbotClient.enviarMensaje(
                request.getSesionId(),
                request.getMensaje()
        );

        return ResponseEntity.ok(response);
    }

    // ── DELETE /api/chat/sesion/{sesionId} ────────────────────────────────────
    //
    // Cierra una sesión de chat y limpia el estado en el chatbot Python
    @DeleteMapping("/sesion/{sesionId}")
    public ResponseEntity<Map<String, String>> cerrarSesion(@PathVariable String sesionId) {
        chatbotClient.cerrarSesion(sesionId);
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente"));
    }
}