package com.tonelli.banking_api;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

// Ejercicios prácticos de Streams y Lambdas
// Cada test es un ejercicio independiente
// Correlos con click derecho → Run
class StreamsEjerciciosTest {

    // ── EJERCICIO 1 ────────────────────────────────────────────────────────────
    // Filtrar números pares de una lista

    @Test
    void ejercicio1_filtrarPares() {
        List<Integer> numeros = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> pares = numeros.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());

        assertEquals(List.of(2, 4, 6, 8, 10), pares);
    }

    // ── EJERCICIO 2 ────────────────────────────────────────────────────────────
    // Convertir lista de nombres a mayúsculas

    @Test
    void ejercicio2_convertirMayusculas() {
        List<String> nombres = List.of("javier", "juan", "maría");

        List<String> mayusculas = nombres.stream()
                .map(nombre -> nombre.toUpperCase())
                .collect(Collectors.toList());

        assertEquals(List.of("JAVIER", "JUAN", "MARÍA"), mayusculas);
    }

    // ── EJERCICIO 3 ────────────────────────────────────────────────────────────
    // Filtrar y mapear — solo nombres con más de 4 letras, en mayúsculas

    @Test
    void ejercicio3_filtrarYMapear() {
        List<String> nombres = List.of("Ana", "Javier", "Juan", "María", "Lu");

        List<String> resultado = nombres.stream()
                .filter(nombre -> nombre.length() > 4)
                .map(nombre -> nombre.toUpperCase())
                .collect(Collectors.toList());

        assertEquals(List.of("JAVIER", "MARÍA"), resultado);
    }

    // ── EJERCICIO 4 ────────────────────────────────────────────────────────────
    // Sumar todos los montos de una lista — caso bancario real

    @Test
    void ejercicio4_sumarMontos() {
        List<Double> montos = List.of(1000.0, 2000.0, 500.0, 3000.0);

        Double total = montos.stream()
                .mapToDouble(m -> m)
                .sum();

        assertEquals(6500.0, total);
    }

    // ── EJERCICIO 5 ────────────────────────────────────────────────────────────
    // Contar movimientos negativos — caso bancario real

    @Test
    void ejercicio5_contarNegativos() {
        List<Double> montos = List.of(1000.0, -500.0, -2000.0, 3000.0, -100.0);

        long negativos = montos.stream()
                .filter(m -> m < 0)
                .count();

        assertEquals(3, negativos);
    }

    // ── EJERCICIO 6 ────────────────────────────────────────────────────────────
    // anyMatch — ¿hay algún monto mayor a 5000?

    @Test
    void ejercicio6_anyMatch() {
        List<Double> montos = List.of(1000.0, 2000.0, 6000.0, 500.0);

        boolean hayMayorA5000 = montos.stream()
                .anyMatch(m -> m > 5000);

        assertTrue(hayMayorA5000);
    }

    // ── EJERCICIO 7 ────────────────────────────────────────────────────────────
    // Unir nombres en un string — útil para logs y mensajes

    @Test
    void ejercicio7_joining() {
        List<String> nombres = List.of("Javier", "Juan", "María");

        String resultado = nombres.stream()
                .collect(Collectors.joining(", "));

        assertEquals("Javier, Juan, María", resultado);
    }
}