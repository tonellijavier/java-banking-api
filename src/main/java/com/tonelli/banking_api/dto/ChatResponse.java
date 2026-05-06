package com.tonelli.banking_api.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

// Lo que devuelve POST /api/chat/mensaje
// Java recibe la respuesta del chatbot Python y la reenvía al frontend
@Data
@AllArgsConstructor
public class ChatResponse {
    private String sesionId;   // para que el frontend mantenga la sesión
    private String respuesta;  // la respuesta del chatbot
    private String esperando;  // estado del chatbot: "" / "destinatario" / "monto" / etc.
}