package com.tonelli.banking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

// Lo que devuelve GET /api/clientes/{dni}/movimientos
// Una línea por cada movimiento del cliente
@Data
@AllArgsConstructor
public class MovimientoResponse {
    private LocalDateTime fecha;
    private String descripcion;
    private Double monto;
}