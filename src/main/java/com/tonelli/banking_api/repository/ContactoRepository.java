package com.tonelli.banking_api.repository;

import com.tonelli.banking_api.model.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Integer> {

    // Busca un contacto por CBU — para verificar que el destinatario existe
    Optional<Contacto> findByCbu(String cbu);

    // Busca contactos habilitados para un cliente específico
    // Seguridad: solo podés transferir a tus propios contactos
    Optional<Contacto> findByDniClienteAndCbu(String dniCliente, String cbu);
}