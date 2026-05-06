package com.tonelli.banking_api.model;

import jakarta.persistence.*;
import lombok.Data;

// Representa la tabla contactos en Neon
// Son los destinatarios habilitados para recibir transferencias
@Entity
@Data
@Table(name = "contactos")
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // DNI del cliente dueño de este contacto
    @Column(name = "dni_cliente")
    private String dniCliente;

    private String nombre;
    private String cbu;
    private String alias;
}