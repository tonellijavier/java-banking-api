package com.tonelli.banking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Lo que devuelve GET /api/clientes/{dni}/saldo
// Solo exponemos nombre y saldo — no el ID interno ni datos sensibles
@Data
@AllArgsConstructor  // Lombok genera el constructor con todos los campos
public class SaldoResponse {
    private String nombre;
    private Double saldo;
    private String productos;
}