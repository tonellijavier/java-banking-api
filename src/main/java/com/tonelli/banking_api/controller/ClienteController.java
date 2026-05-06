package com.tonelli.banking_api.controller;

import com.tonelli.banking_api.dto.MovimientoResponse;
import com.tonelli.banking_api.dto.SaldoResponse;
import com.tonelli.banking_api.dto.TransferenciaRequest;
import com.tonelli.banking_api.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController combina @Controller + @ResponseBody
// Le dice a Spring que esta clase maneja requests HTTP
// y que las respuestas se convierten a JSON automáticamente
//
// @RequestMapping define la ruta base para todos los endpoints de esta clase
@RestController
@RequestMapping("/api")
public class ClienteController {

    // Spring inyecta el Service automáticamente
    // No hacemos new ClienteService() — Spring lo gestiona
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // ── GET /api/clientes/{dni}/saldo ──────────────────────────────────────────
    //
    // Devuelve el saldo y productos del cliente
    // Equivalente a GET /sesion/{id} en el chatbot Python con FastAPI
    //
    // Ejemplo: GET /api/clientes/12345678/saldo
    // Respuesta: {"nombre": "Javier", "saldo": 93000.0, "productos": "..."}
    @GetMapping("/clientes/{dni}/saldo")
    public ResponseEntity<SaldoResponse> getSaldo(@PathVariable String dni) {
        SaldoResponse response = clienteService.obtenerSaldo(dni);
        return ResponseEntity.ok(response);
    }

    // ── GET /api/clientes/{dni}/movimientos ────────────────────────────────────
    //
    // Devuelve el historial de movimientos del cliente ordenado por fecha
    //
    // Ejemplo: GET /api/clientes/12345678/movimientos
    // Respuesta: [{"fecha": "...", "descripcion": "...", "monto": -5000.0}, ...]
    @GetMapping("/clientes/{dni}/movimientos")
    public ResponseEntity<List<MovimientoResponse>> getMovimientos(@PathVariable String dni) {
        List<MovimientoResponse> movimientos = clienteService.obtenerMovimientos(dni);
        return ResponseEntity.ok(movimientos);
    }

    // ── POST /api/transferencias ───────────────────────────────────────────────
    //
    // Ejecuta una transferencia entre dos clientes
    // Jackson convierte el JSON del body a TransferenciaRequest automáticamente
    //
    // Ejemplo: POST /api/transferencias
    // Body: {"dniOrigen": "12345678", "dniDestino": "87654321", "monto": 5000}
    // Respuesta: saldo actualizado del cliente origen
    @PostMapping("/transferencias")
    public ResponseEntity<SaldoResponse> transferir(@RequestBody TransferenciaRequest request) {
        SaldoResponse response = clienteService.ejecutarTransferencia(request);
        return ResponseEntity.ok(response);
    }
}