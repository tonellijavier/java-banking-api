package com.tonelli.banking_api.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "clientes")
public class Cliente {

    // La clave primaria es el DNI — no hay columna id en esta tabla
    @Id
    @Column(name = "dni")
    private String dni;

    private String nombre;
    private Double saldo;

    // productos es text[] en PostgreSQL — lo leemos pero no lo modificamos
// insertable=false, updatable=false le dice a Hibernate que no lo incluya
// en los INSERT ni UPDATE — evita el conflicto de tipos
    @Column(name = "productos", insertable = false, updatable = false)
    private String productos;
}