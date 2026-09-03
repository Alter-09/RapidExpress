package com.RapidExpress;

import com.RapidExpress.exception.*;
import com.RapidExpress.model.enums.TipoLicencia;
import com.RapidExpress.util.DataValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la validación y gestión de datos aceptados por RapidExpress.
 * Valida formatos, rangos, obligatoriedad y ejemplos guiados por database/README.md.
 */
public class DataValidationTest {

    @Test
    @DisplayName("Placas válidas representativas de README.md son aceptadas")
    void testPlacasValidas() {
        assertDoesNotThrow(() -> {
            assertEquals("ABC-123", DataValidator.validarPlaca("ABC-123"));
            assertEquals("ABC-125", DataValidator.validarPlaca("abc-125")); // Auto-uppercase
            assertEquals("ABC-140", DataValidator.validarPlaca(" ABC-140 ")); // Trim
            assertEquals("ABC-132", DataValidator.validarPlaca("ABC-132"));
        });
    }

    @Test
    @DisplayName("Placas inválidas o vacías lanzan DatoRequeridoException o FormatoInvalidoException")
    void testPlacasInvalidas() {
        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarPlaca(""));
        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarPlaca(null));

        FormatoInvalidoException ex = assertThrows(FormatoInvalidoException.class,
                () -> DataValidator.validarPlaca("ABC-123456789"));
        assertTrue(ex.getMessage().contains("ABC-123"));
        assertEquals("Placa", ex.getCampo());
        assertEquals("ABC-123", ex.getEjemploAceptado());
    }

    @Test
    @DisplayName("Año de fabricación respeta chk_vehiculo_anio [1990 - 2030]")
    void testAnioVehiculo() {
        assertDoesNotThrow(() -> {
            assertEquals(2020, DataValidator.validarAnioVehiculo(2020));
            assertEquals(2018, DataValidator.validarAnioVehiculo(2018));
            assertEquals(2023, DataValidator.validarAnioVehiculo(2023));
            assertEquals(1990, DataValidator.validarAnioVehiculo(1990));
            assertEquals(2030, DataValidator.validarAnioVehiculo(2030));
        });

        RangoInvalidoException exBajo = assertThrows(RangoInvalidoException.class,
                () -> DataValidator.validarAnioVehiculo(1989));
        assertTrue(exBajo.getMessage().contains("2020"));

        RangoInvalidoException exAlto = assertThrows(RangoInvalidoException.class,
                () -> DataValidator.validarAnioVehiculo(2031));
        assertTrue(exAlto.getMessage().contains("2020"));
    }

    @Test
    @DisplayName("Capacidad de carga respeta chk_vehiculo_capacidad (> 0 kg)")
    void testCapacidadVehiculo() {
        assertDoesNotThrow(() -> {
            assertEquals(new BigDecimal("5000.00"), DataValidator.validarCapacidadVehiculo(new BigDecimal("5000.00")));
            assertEquals(new BigDecimal("7000.00"), DataValidator.validarCapacidadVehiculo(new BigDecimal("7000.00")));
            assertEquals(new BigDecimal("22000.00"), DataValidator.validarCapacidadVehiculo(new BigDecimal("22000.00")));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarCapacidadVehiculo(null));

        RangoInvalidoException exCero = assertThrows(RangoInvalidoException.class,
                () -> DataValidator.validarCapacidadVehiculo(BigDecimal.ZERO));
        assertTrue(exCero.getMessage().contains("5000.00"));

        assertThrows(RangoInvalidoException.class,
                () -> DataValidator.validarCapacidadVehiculo(new BigDecimal("-500.00")));
    }

    @Test
    @DisplayName("Identificación de conductor respeta VARCHAR(20), formato numérico y rechaza datos sin sentido")
    void testIdentificacionConductor() {
        assertDoesNotThrow(() -> {
            assertEquals("1000000001", DataValidator.validarIdentificacionConductor("1000000001"));
            assertEquals("1000000003", DataValidator.validarIdentificacionConductor(" 1000000003 "));
            assertEquals("1000000021", DataValidator.validarIdentificacionConductor("1000000021"));
            assertEquals("1001", DataValidator.validarIdentificacionConductor("1001"));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarIdentificacionConductor(""));
        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarIdentificacionConductor(null));

        // Rechaza letras, símbolos y datos sin sentido
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarIdentificacionConductor("abc"));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarIdentificacionConductor("!@#$%"));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarIdentificacionConductor("-100000001"));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarIdentificacionConductor("0000000000"));
        assertThrows(FormatoInvalidoException.class,
                () -> DataValidator.validarIdentificacionConductor("123456789012345678901")); // 21 chars
    }

    @Test
    @DisplayName("Nombre completo valida personas reales y rechaza datos sin sentido")
    void testNombreCompletoConductor() {
        assertDoesNotThrow(() -> {
            assertEquals("Carlos Andres Rodriguez", DataValidator.validarNombreCompleto("Carlos Andres Rodriguez"));
            assertEquals("María Fernanda Gómez", DataValidator.validarNombreCompleto("  María Fernanda Gómez  "));
            assertEquals("Juan Carlos Martínez", DataValidator.validarNombreCompleto("Juan Carlos Martínez"));
            assertEquals("Jorge Eliecer Gaitan", DataValidator.validarNombreCompleto("Jorge Eliecer Gaitan"));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarNombreCompleto(""));
        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarNombreCompleto(null));

        // Rechaza números, símbolos, un solo nombre o caracteres sin sentido
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarNombreCompleto("123456"));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarNombreCompleto("Carlos123 Rodriguez"));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarNombreCompleto("Carlos")); // Falta apellido
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarNombreCompleto("!@#$%^&*()"));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarNombreCompleto("A".repeat(101)));
    }

    @Test
    @DisplayName("Tipo de licencia valida categorías de transporte pesado C, D, E según database/README.md")
    void testTipoLicenciaConductor() {
        assertDoesNotThrow(() -> {
            assertEquals(TipoLicencia.C, DataValidator.validarTipoLicenciaConductor(TipoLicencia.C));
            assertEquals(TipoLicencia.D, DataValidator.validarTipoLicenciaConductor(TipoLicencia.D));
            assertEquals(TipoLicencia.E, DataValidator.validarTipoLicenciaConductor(TipoLicencia.E));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarTipoLicenciaConductor(null));

        // No válidas para conductores de carga pesada de RapidExpress (A: Motos, B: Carros particulares)
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarTipoLicenciaConductor(TipoLicencia.A));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarTipoLicenciaConductor(TipoLicencia.B));
    }

    @Test
    @DisplayName("Contacto telefónico valida formato numérico")
    void testContactoTelefonico() {
        assertDoesNotThrow(() -> {
            assertEquals("3001112233", DataValidator.validarContacto("3001112233", "Contacto"));
            assertEquals("3001112255", DataValidator.validarContacto("3001112255", "Contacto"));
            assertEquals("3001112231", DataValidator.validarContacto(" 300 111 2231 ", "Contacto"));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarContacto("", "Contacto"));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarContacto("telefono_invalido", "Contacto"));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarContacto("123", "Contacto"));
    }

    @Test
    @DisplayName("ID de seguimiento valida formato PKG-YYYYMMDD-XXX y longitud VARCHAR(36)")
    void testIdSeguimiento() {
        assertDoesNotThrow(() -> {
            assertEquals("PKG-20240115-001", DataValidator.validarIdSeguimiento("PKG-20240115-001"));
            assertEquals("PKG-20240115-003", DataValidator.validarIdSeguimiento("pkg-20240115-003"));
            assertEquals("PKG-20240116-006", DataValidator.validarIdSeguimiento("PKG-20240116-006"));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarIdSeguimiento(""));
        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarIdSeguimiento(null));

        assertThrows(FormatoInvalidoException.class,
                () -> DataValidator.validarIdSeguimiento("A".repeat(37)));
    }

    @Test
    @DisplayName("Peso de paquete respeta chk_paquete_peso (> 0 kg)")
    void testPesoPaquete() {
        assertDoesNotThrow(() -> {
            assertEquals(new BigDecimal("1.20"), DataValidator.validarPesoPaquete(new BigDecimal("1.20")));
            assertEquals(new BigDecimal("2.50"), DataValidator.validarPesoPaquete(new BigDecimal("2.50")));
            assertEquals(new BigDecimal("22.61"), DataValidator.validarPesoPaquete(new BigDecimal("22.61")));
            assertEquals(new BigDecimal("200.00"), DataValidator.validarPesoPaquete(new BigDecimal("200.00")));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarPesoPaquete(null));
        assertThrows(RangoInvalidoException.class, () -> DataValidator.validarPesoPaquete(BigDecimal.ZERO));
        assertThrows(RangoInvalidoException.class, () -> DataValidator.validarPesoPaquete(new BigDecimal("-1.50")));
    }

    @Test
    @DisplayName("Dimensiones de paquete validan formato AxAxP según README.md")
    void testDimensionesPaquete() {
        assertDoesNotThrow(() -> {
            assertEquals("30x20x5", DataValidator.validarDimensiones("30x20x5"));
            assertEquals("40x30x20", DataValidator.validarDimensiones(" 40x30x20 "));
            assertEquals("60x40x40", DataValidator.validarDimensiones("60x40x40"));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarDimensiones(""));
        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarDimensiones(null));
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarDimensiones("30x20")); // falta profundidad
        assertThrows(FormatoInvalidoException.class, () -> DataValidator.validarDimensiones("grande"));
    }

    @Test
    @DisplayName("Costo de mantenimiento es mayor o igual a 0 COP")
    void testCostoMantenimiento() {
        assertDoesNotThrow(() -> {
            assertEquals(new BigDecimal("450000.00"), DataValidator.validarCostoMantenimiento(new BigDecimal("450000.00")));
            assertEquals(new BigDecimal("680000.00"), DataValidator.validarCostoMantenimiento(new BigDecimal("680000.00")));
            assertEquals(new BigDecimal("1850000.00"), DataValidator.validarCostoMantenimiento(new BigDecimal("1850000.00")));
        });

        assertThrows(DatoRequeridoException.class, () -> DataValidator.validarCostoMantenimiento(null));
        assertThrows(RangoInvalidoException.class, () -> DataValidator.validarCostoMantenimiento(new BigDecimal("-10.00")));
    }

    @Test
    @DisplayName("Kilometraje es opcional y no negativo")
    void testKilometraje() {
        assertDoesNotThrow(() -> {
            assertNull(DataValidator.validarKilometraje(null));
            assertEquals(0, DataValidator.validarKilometraje(0));
            assertEquals(150000, DataValidator.validarKilometraje(150000));
        });

        assertThrows(RangoInvalidoException.class, () -> DataValidator.validarKilometraje(-1));
    }

    @Test
    @DisplayName("Jerarquía de excepciones y mensajes con ejemplo")
    void testExcepcionesJerarquia() {
        FormatoInvalidoException fie = new FormatoInvalidoException("Placa", "XYZ", "3 letras - 3 números", "ABC-123");
        assertTrue(fie instanceof ValidacionException);
        assertTrue(fie instanceof RapidExpressException);
        assertEquals("Placa", fie.getCampo());
        assertEquals("ABC-123", fie.getEjemploAceptado());
        assertTrue(fie.getMessage().contains("ABC-123"));

        RangoInvalidoException rie = new RangoInvalidoException("Capacidad", -5, "> 0", "22000", "5000.00");
        assertTrue(rie instanceof ValidacionException);
        assertEquals("Capacidad", rie.getCampo());
        assertEquals("5000.00", rie.getEjemploAceptado());
        assertTrue(rie.getMessage().contains("5000.00"));

        DatoRequeridoException dre = new DatoRequeridoException("Marca", "Chevrolet");
        assertTrue(dre instanceof ValidacionException);
        assertEquals("Marca", dre.getCampo());
        assertEquals("Chevrolet", dre.getEjemploAceptado());
        assertTrue(dre.getMessage().contains("Chevrolet"));
    }
}
