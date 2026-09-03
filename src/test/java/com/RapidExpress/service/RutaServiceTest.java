package com.RapidExpress.service;

import com.RapidExpress.dao.*;
import com.RapidExpress.exception.CapacidadExcedidaException;
import com.RapidExpress.exception.ValidacionException;
import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.EstadoPaquete;
import com.RapidExpress.model.enums.EstadoVehiculo;
import com.RapidExpress.model.enums.TipoLicencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RutaServiceTest {

    @Test
    @DisplayName("Fallo 2: Validar que crear ruta sin paquetes lance ValidacionException")
    void testCrearRutaSinPaquetesLanzaExcepcion() {
        RutaService rutaService = new RutaService();
        assertThrows(ValidacionException.class, () -> {
            rutaService.crearHojaDeRuta(LocalDate.now(), "ABC-123", "1000000001", List.of(), "TEST_USER");
        });
    }

    @Test
    @DisplayName("Fallo 2: Validar que si el peso total excede la capacidad del vehículo se lance CapacidadExcedidaException")
    void testCapacidadExcedidaLanzaExcepcion() {
        // Mock VehiculoDAO que retorna un vehículo con capacidad máxima de 100 kg
        VehiculoDAO mockVehiculoDAO = new VehiculoDAO() {
            @Override
            public Optional<Vehiculo> findById(String placa) {
                Vehiculo v = new Vehiculo("ABC-123", "Chevrolet", "NPR", 2022, new BigDecimal("100.00"), EstadoVehiculo.DISPONIBLE);
                return Optional.of(v);
            }
        };

        // Mock ConductorDAO que retorna un conductor Activo
        ConductorDAO mockConductorDAO = new ConductorDAO() {
            @Override
            public Optional<Conductor> findById(String id) {
                Conductor c = new Conductor("1000000001", "Juan Perez", TipoLicencia.C, "3001234567", EstadoConductor.ACTIVO, null);
                return Optional.of(c);
            }
        };

        // Mock PaqueteDAO que retorna paquetes con peso total 150 kg (mayor a 100 kg)
        PaqueteDAO mockPaqueteDAO = new PaqueteDAO() {
            @Override
            public Optional<Paquete> findById(String id) {
                Paquete p = new Paquete(
                        id, "Caja pesada", new BigDecimal("150.00"), "50x50x50",
                        "Origen", "Destino", "Remitente", "3001111111", "Dir Rem",
                        "Destinatario", "3002222222", "Dir Dest", EstadoPaquete.EN_BODEGA
                );
                return Optional.of(p);
            }
        };

        RutaDAO mockRutaDAO = new RutaDAO();
        RutaPaqueteDAO mockRutaPaqueteDAO = new RutaPaqueteDAO();
        AuditoriaService mockAuditoriaService = new AuditoriaService();

        RutaService rutaService = new RutaService(
                mockRutaDAO, mockRutaPaqueteDAO, mockVehiculoDAO,
                mockConductorDAO, mockPaqueteDAO, mockAuditoriaService
        );

        CapacidadExcedidaException ex = assertThrows(CapacidadExcedidaException.class, () -> {
            rutaService.crearHojaDeRuta(
                    LocalDate.now(), "ABC-123", "1000000001",
                    List.of("PKG-001"), "TEST_USER"
            );
        });

        assertEquals(new BigDecimal("150.00"), ex.getPesoTotal());
        assertEquals(new BigDecimal("100.00"), ex.getCapacidadMaxima());
    }
}
