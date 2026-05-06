package com.tonelli.banking_api.service;

import com.tonelli.banking_api.dto.MovimientoResponse;
import com.tonelli.banking_api.dto.SaldoResponse;
import com.tonelli.banking_api.dto.TransferenciaRequest;
import com.tonelli.banking_api.model.Cliente;
import com.tonelli.banking_api.model.Contacto;
import com.tonelli.banking_api.model.Movimiento;
import com.tonelli.banking_api.repository.ClienteRepository;
import com.tonelli.banking_api.repository.ContactoRepository;
import com.tonelli.banking_api.repository.MovimientoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// @Service le dice a Spring que esta clase contiene lógica de negocio
// Spring la registra y permite inyectarla en el Controller
@Service
public class ClienteService {

    // Spring inyecta los repositories automáticamente
    // No necesitamos hacer new XxxRepository() — Spring lo gestiona
    private final ClienteRepository clienteRepository;
    private final MovimientoRepository movimientoRepository;
    private final ContactoRepository contactoRepository;

    public ClienteService(ClienteRepository clienteRepository,
                          MovimientoRepository movimientoRepository,
                          ContactoRepository contactoRepository) {
        this.clienteRepository = clienteRepository;
        this.movimientoRepository = movimientoRepository;
        this.contactoRepository = contactoRepository;
    }

    // ── CONSULTAR SALDO ────────────────────────────────────────────────────────

    public SaldoResponse obtenerSaldo(String dni) {
        // Buscamos el cliente por DNI — si no existe lanzamos excepción
        Cliente cliente = clienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dni));

        // Convertimos el Model a DTO — solo exponemos lo necesario
        return new SaldoResponse(
                cliente.getNombre(),
                cliente.getSaldo(),
                cliente.getProductos()
        );
    }

    // ── CONSULTAR MOVIMIENTOS ──────────────────────────────────────────────────

    public List<MovimientoResponse> obtenerMovimientos(String dni) {
        // Verificamos que el cliente existe antes de buscar sus movimientos
        clienteRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado: " + dni));

        // Buscamos los movimientos ordenados por fecha descendente
        List<Movimiento> movimientos = movimientoRepository
                .findByDniClienteOrderByFechaDesc(dni);

        // Convertimos cada Movimiento (Model) a MovimientoResponse (DTO)
        // Stream + map es el equivalente a list comprehension en Python
        return movimientos.stream()
                .map(m -> new MovimientoResponse(
                        m.getFecha(),
                        m.getDescripcion(),
                        m.getMonto()
                ))
                .collect(Collectors.toList());
    }

    // ── EJECUTAR TRANSFERENCIA ─────────────────────────────────────────────────

    // @Transactional garantiza que si algo falla a mitad,
    // todos los cambios se revierten — o todo o nada
    // Fundamental en operaciones financieras
    @Transactional
    public SaldoResponse ejecutarTransferencia(TransferenciaRequest request) {

        // Validación 1 — el cliente origen debe existir
        Cliente origen = clienteRepository.findByDni(request.getDniOrigen())
                .orElseThrow(() -> new RuntimeException("Cliente origen no encontrado"));

        // Validación 2 — el CBU debe corresponder a un contacto habilitado del cliente
        // Seguridad: no podés transferir a cualquier CBU, solo a tus contactos registrados
        // Esto evita que alguien inyecte un CBU arbitrario en el request
        Contacto contacto = contactoRepository
                .findByDniClienteAndCbu(request.getDniOrigen(), request.getCbuDestino())
                .orElseThrow(() -> new RuntimeException("Contacto no encontrado o no habilitado"));

        // Validación 3 — saldo suficiente
        if (origen.getSaldo() < request.getMonto()) {
            throw new RuntimeException("Saldo insuficiente");
        }

        // Validación 4 — monto positivo
        if (request.getMonto() <= 0) {
            throw new RuntimeException("El monto debe ser mayor a cero");
        }

        // Ejecutamos la transferencia — descontamos del origen
        // El dinero sale de la cuenta del cliente
        origen.setSaldo(origen.getSaldo() - request.getMonto());
        clienteRepository.save(origen);

        // Registramos el movimiento de salida en el historial
        // El monto es negativo — indica salida de dinero
        Movimiento movOrigen = new Movimiento();
        movOrigen.setDniCliente(request.getDniOrigen());
        movOrigen.setFecha(LocalDateTime.now());
        movOrigen.setDescripcion("Transferencia a " + contacto.getNombre());
        movOrigen.setMonto(-request.getMonto());
        movimientoRepository.save(movOrigen);

        // Devolvemos el saldo actualizado del cliente origen
        return new SaldoResponse(
                origen.getNombre(),
                origen.getSaldo(),
                origen.getProductos()
        );
    }
}