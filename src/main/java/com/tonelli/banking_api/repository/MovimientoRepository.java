package com.tonelli.banking_api.repository;

import com.tonelli.banking_api.model.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// Igual que ClienteRepository pero para movimientos
@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Spring genera: SELECT * FROM movimientos WHERE dni_cliente = ?
    // ORDER BY fecha DESC no está soportado así directamente
    // pero podemos ordenar en el Service
    List<Movimiento> findByDniClienteOrderByFechaDesc(String dniCliente);
}