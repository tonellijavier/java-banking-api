package com.tonelli.banking_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

// Representa la tabla movimientos en Neon
// Cada fila es una operación registrada — transferencia, depósito, etc.
@Entity
@Data
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // dni_cliente conecta este movimiento con un cliente
    // No usamos @ManyToOne para mantenerlo simple por ahora
    @Column(name = "dni_cliente")
    private String dniCliente;

    private LocalDateTime fecha;
    private String descripcion;
    private Double monto;
}