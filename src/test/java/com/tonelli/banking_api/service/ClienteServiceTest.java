package com.tonelli.banking_api.service;

import com.tonelli.banking_api.dto.SaldoResponse;
import com.tonelli.banking_api.dto.TransferenciaRequest;
import com.tonelli.banking_api.model.Cliente;
import com.tonelli.banking_api.model.Contacto;
import com.tonelli.banking_api.repository.ClienteRepository;
import com.tonelli.banking_api.repository.ContactoRepository;
import com.tonelli.banking_api.repository.MovimientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

// @ExtendWith activa Mockito para esta clase de tests
// Sin esto @Mock e @InjectMocks no funcionan
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    // @Mock crea objetos falsos de los repositories
    // No hablan con Neon — están en memoria
    @Mock
    ClienteRepository clienteRepository;

    @Mock
    MovimientoRepository movimientoRepository;

    @Mock
    ContactoRepository contactoRepository;

    // @InjectMocks crea el ClienteService real
    // pero con los mocks inyectados en lugar de los repositories reales
    @InjectMocks
    ClienteService clienteService;


    // ── TEST 1 ─────────────────────────────────────────────────────────────────
    // Escenario feliz — el cliente existe y devuelve su saldo

    @Test
    void obtenerSaldo_clienteExistente_devuelveSaldo() {

        // ARRANGE — armamos el escenario
        // Creamos un cliente falso con datos conocidos
        Cliente clienteFalso = new Cliente();
        clienteFalso.setDni("12345678");
        clienteFalso.setNombre("Javier");
        clienteFalso.setSaldo(84000.0);

        // Le decimos al mock qué devolver cuando llamen a findByDni
        // Si no configuramos esto, devuelve Optional.empty() por defecto
        when(clienteRepository.findByDni("12345678"))
                .thenReturn(Optional.of(clienteFalso));

        // ACT — ejecutamos el método que queremos testear
        SaldoResponse resultado = clienteService.obtenerSaldo("12345678");

        // ASSERT — verificamos que el resultado es correcto
        assertEquals("Javier", resultado.getNombre());
        assertEquals(84000.0, resultado.getSaldo());
    }


    // ── TEST 2 ─────────────────────────────────────────────────────────────────
    // Escenario alternativo — el cliente no existe, debe lanzar excepción

    @Test
    void obtenerSaldo_clienteNoExistente_lanzaExcepcion() {

        // ARRANGE — el mock devuelve vacío — el cliente no existe
        when(clienteRepository.findByDni("99999999"))
                .thenReturn(Optional.empty());

        // ACT + ASSERT — verificamos que lanza RuntimeException
        // assertThrows verifica que el código dentro del lambda lanza la excepción esperada
        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> clienteService.obtenerSaldo("99999999")
        );

        // Verificamos que el mensaje de la excepción es el correcto
        assertTrue(excepcion.getMessage().contains("no encontrado"));
    }


    // ── TEST 3 ─────────────────────────────────────────────────────────────────
    // Transferencia exitosa — saldo suficiente y contacto habilitado

    @Test
    void ejecutarTransferencia_exitosa_devuelveSaldoActualizado() {

        // ARRANGE
        Cliente origen = new Cliente();
        origen.setDni("12345678");
        origen.setNombre("Javier");
        origen.setSaldo(84000.0);

        // El contacto destino habilitado para este cliente
        Contacto contacto = new Contacto();
        contacto.setNombre("Juan Pérez");
        contacto.setCbu("0000003100025786490015");
        contacto.setDniCliente("12345678");

        // Configuramos los mocks
        when(clienteRepository.findByDni("12345678"))
                .thenReturn(Optional.of(origen));
        when(contactoRepository.findByDniClienteAndCbu("12345678", "0000003100025786490015"))
                .thenReturn(Optional.of(contacto));

        // Armamos el request
        TransferenciaRequest request = new TransferenciaRequest();
        request.setDniOrigen("12345678");
        request.setCbuDestino("0000003100025786490015");
        request.setMonto(1000.0);

        // ACT
        SaldoResponse resultado = clienteService.ejecutarTransferencia(request);

        // ASSERT — el saldo debe haber bajado 1000
        assertEquals(83000.0, resultado.getSaldo());
        assertEquals("Javier", resultado.getNombre());

        // Verificamos que se guardó el movimiento
        // verify confirma que save() fue llamado al menos una vez
        verify(movimientoRepository, atLeastOnce()).save(any());
    }


    // ── TEST 4 ─────────────────────────────────────────────────────────────────
    // Transferencia fallida — saldo insuficiente

    @Test
    void ejecutarTransferencia_saldoInsuficiente_lanzaExcepcion() {

        // ARRANGE — cliente con saldo bajo
        Cliente origen = new Cliente();
        origen.setDni("12345678");
        origen.setNombre("Javier");
        origen.setSaldo(500.0);  // saldo bajo

        Contacto contacto = new Contacto();
        contacto.setNombre("Juan Pérez");
        contacto.setCbu("0000003100025786490015");
        contacto.setDniCliente("12345678");

        when(clienteRepository.findByDni("12345678"))
                .thenReturn(Optional.of(origen));
        when(contactoRepository.findByDniClienteAndCbu("12345678", "0000003100025786490015"))
                .thenReturn(Optional.of(contacto));

        TransferenciaRequest request = new TransferenciaRequest();
        request.setDniOrigen("12345678");
        request.setCbuDestino("0000003100025786490015");
        request.setMonto(1000.0);  // monto mayor al saldo

        // ACT + ASSERT — debe lanzar excepción de saldo insuficiente
        RuntimeException excepcion = assertThrows(
                RuntimeException.class,
                () -> clienteService.ejecutarTransferencia(request)
        );

        assertTrue(excepcion.getMessage().contains("insuficiente"));

        // Verificamos que NO se guardó nada — la transacción se revirtió
        verify(movimientoRepository, never()).save(any());
    }
}