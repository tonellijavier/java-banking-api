package com.tonelli.banking_api.dto;

import lombok.Data;

// Lo que recibe POST /api/chat/mensaje
// El frontend manda el DNI del cliente y su mensaje
// Java valida el DNI y reenvía el mensaje al chatbot Python
@Data
public class ChatRequest {
    private String dni;       // identifica al cliente
    private String sesionId;  // identifica la conversación
    private String mensaje;   // lo que escribió el usuario
}