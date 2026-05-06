package com.tonelli.banking_api.dto;

import lombok.Data;

// Recibe CBU del destinatario — igual que el sistema Python
// que busca contactos por nombre/alias y usa su CBU

@Data
public class TransferenciaRequest {
    private String dniOrigen;
    private String cbuDestino;  // CBU del contacto destinatario
    private Double monto;
}