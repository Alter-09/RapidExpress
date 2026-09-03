package com.RapidExpress;

import com.RapidExpress.config.DatabaseConnection;
import com.RapidExpress.model.enums.*;
import com.RapidExpress.model.entity.*;
import com.RapidExpress.exception.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class RapidExpressCoreTest {

    @Test
    @DisplayName("DatabaseConnection builds correct URL and default parameters")
    void testDatabaseUrl() {
        String url = DatabaseConnection.getUrl();
        assertNotNull(url);
        assertTrue(url.startsWith("jdbc:mysql://"));
        assertEquals("root", DatabaseConnection.getUser());
    }

    @Test
    @DisplayName("DatabaseConnection handles testConnection gracefully")
    void testConnectionGraceful() {
        assertDoesNotThrow(() -> {
            boolean connected = DatabaseConnection.testConnection();
            System.out.println("[TEST] MySQL connection test completed: " + (connected ? "CONNECTED" : "OFFLINE/UNAVAILABLE"));
        });
    }

    @Test
    @DisplayName("EstadoVehiculo parsing and descriptions")
    void testEstadoVehiculo() {
        assertEquals("Disponible", EstadoVehiculo.DISPONIBLE.getDescripcion());
        assertEquals(EstadoVehiculo.EN_RUTA, EstadoVehiculo.fromString("En_Ruta"));
        assertThrows(IllegalArgumentException.class, () -> EstadoVehiculo.fromString("Inexistente"));
    }

    @Test
    @DisplayName("EstadoConductor parsing and descriptions")
    void testEstadoConductor() {
        assertEquals("Activo", EstadoConductor.ACTIVO.getDescripcion());
        assertEquals(EstadoConductor.DE_VACACIONES, EstadoConductor.fromString("De_Vacaciones"));
        assertThrows(IllegalArgumentException.class, () -> EstadoConductor.fromString("Desconocido"));
    }

    @Test
    @DisplayName("EstadoPaquete parsing and descriptions")
    void testEstadoPaquete() {
        assertEquals("En_Bodega", EstadoPaquete.EN_BODEGA.getDescripcion());
        assertEquals(EstadoPaquete.ENTREGADO, EstadoPaquete.fromString("Entregado"));
        assertThrows(IllegalArgumentException.class, () -> EstadoPaquete.fromString("Invalido"));
    }

    @Test
    @DisplayName("EstadoRuta parsing and descriptions")
    void testEstadoRuta() {
        assertEquals("Planificada", EstadoRuta.PLANIFICADA.getDescripcion());
        assertEquals(EstadoRuta.EN_CURSO, EstadoRuta.fromString("En_Curso"));
        assertThrows(IllegalArgumentException.class, () -> EstadoRuta.fromString("Invalido"));
    }

    @Test
    @DisplayName("TipoLicencia parsing and valid codes")
    void testTipoLicencia() {
        assertEquals("B", TipoLicencia.B.getCodigo());
        assertEquals(TipoLicencia.C, TipoLicencia.fromString("C"));
        assertThrows(IllegalArgumentException.class, () -> TipoLicencia.fromString("Z"));
    }

    @Test
    @DisplayName("Model Entities constructors and getters")
    void testModelEntities() {
        Vehiculo v = new Vehiculo("ABC-123", "Chevrolet", "NPR 700", 2020, new BigDecimal("5000.00"), EstadoVehiculo.DISPONIBLE);
        assertEquals("ABC-123", v.getPlaca());
        assertEquals(new BigDecimal("5000.00"), v.getCapacidadMaxKg());

        Conductor c = new Conductor("1001", "Carlos Rodriguez", TipoLicencia.C, "3001234567", EstadoConductor.ACTIVO, "ABC-123");
        assertEquals("1001", c.getNumIdentificacion());
        assertEquals("Carlos Rodriguez", c.getNombreCompleto());

        Paquete p = new Paquete("PK-001", "Caja herramientas", new BigDecimal("15.50"), "30x20x15", "Calle 1", "Calle 2", "Remitente", "3000000", "Dir Rem", "Dest", "3111111", "Dir Dest", EstadoPaquete.EN_BODEGA);
        assertEquals("PK-001", p.getIdSeguimiento());
        assertEquals(EstadoPaquete.EN_BODEGA, p.getEstado());

        Ruta r = new Ruta(1L, LocalDate.now(), "ABC-123", "1001", EstadoRuta.PLANIFICADA);
        assertEquals(1L, r.getIdRuta());
        assertEquals(EstadoRuta.PLANIFICADA, r.getEstado());
    }

    @Test
    @DisplayName("Exceptions contain proper messages")
    void testExceptions() {
        CapacidadExcedidaException ce = new CapacidadExcedidaException("ABC-123", new BigDecimal("5000"), new BigDecimal("6000"));
        assertTrue(ce.getMessage().contains("ABC-123"));

        EntidadDuplicadaException ed = new EntidadDuplicadaException("Vehiculo", "ABC-123");
        assertTrue(ed.getMessage().contains("ABC-123"));

        EntidadNoEncontradaException ene = new EntidadNoEncontradaException("Conductor", "999");
        assertTrue(ene.getMessage().contains("999"));

        EstadoInvalidoException ei = new EstadoInvalidoException("Vehículo no disponible");
        assertEquals("Vehículo no disponible", ei.getMessage());
    }
}