package com.tonelli.banking_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Lo que devuelve GET /api/clientes/{dni}/resumen
// Calcula totales usando Streams sobre la lista de movimientos
@Data
@AllArgsConstructor
public class ResumenResponse {
    private String nombre;
    private Double saldo;
    private Double totalGastado;      // suma de movimientos negativos
    private Double totalRecibido;     // suma de movimientos positivos
    private Long cantidadMovimientos; // total de movimientos
    private String ultimoMovimiento;  // descripción del más reciente
}