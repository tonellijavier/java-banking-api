package com.tonelli.banking_api.repository;

import com.tonelli.banking_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// JpaRepository<Cliente, Long> le dice a Spring:
//   - trabajamos con la entidad Cliente
//   - la clave primaria es de tipo Long
//
// Spring genera automáticamente sin escribir código:
//   findById(), findAll(), save(), delete(), count(), etc.
//
// Solo agregamos los métodos que necesitamos nosotros
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, String> {
    // findByDni ya no es necesario porque dni es el @Id
    // podemos usar findById(dni) directamente
    // pero lo dejamos para no cambiar el Service
    Optional<Cliente> findByDni(String dni);
}