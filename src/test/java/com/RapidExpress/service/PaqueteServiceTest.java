package com.RapidExpress.service;

import com.RapidExpress.exception.ValidacionException;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.enums.EstadoPaquete;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PaqueteServiceTest {

    @Test
    @DisplayName("Fallo 1: Validar que un paquete nulo lance ValidacionException")
    void testValidacionPaqueteNulo() {
        PaqueteService service = new PaqueteService();
        assertThrows(ValidacionException.class, () -> {
            service.registrarPaquete(null, "TEST_USER");
        });
    }

    @Test
    @DisplayName("Fallo 1: Validar que un paquete con peso negativo o cero lance ValidacionException")
    void testValidacionPesoInvalido() {
        PaqueteService service = new PaqueteService();
        Paquete paqueteInvalido = new Paquete(
                null, "Documentos", BigDecimal.ZERO, "10x10x10",
                "Calle 1", "Calle 2", "Remitente", "3001234567", "Dir Rem",
                "Destinatario", "3007654321", "Dir Dest", EstadoPaquete.EN_BODEGA
        );

        assertThrows(ValidacionException.class, () -> {
            service.registrarPaquete(paqueteInvalido, "TEST_USER");
        });
    }

    @Test
    @DisplayName("Fallo 1: Validar que un paquete sin descripción lance ValidacionException")
    void testValidacionDescripcionVacia() {
        PaqueteService service = new PaqueteService();
        Paquete paqueteInvalido = new Paquete(
                null, "   ", new BigDecimal("5.0"), "10x10x10",
                "Calle 1", "Calle 2", "Remitente", "3001234567", "Dir Rem",
                "Destinatario", "3007654321", "Dir Dest", EstadoPaquete.EN_BODEGA
        );

        assertThrows(ValidacionException.class, () -> {
            service.registrarPaquete(paqueteInvalido, "TEST_USER");
        });
    }
}
