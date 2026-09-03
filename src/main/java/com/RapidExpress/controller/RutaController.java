package com.RapidExpress.controller;

import com.RapidExpress.exception.CapacidadExcedidaException;
import com.RapidExpress.exception.RapidExpressException;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.entity.Ruta;
import com.RapidExpress.model.entity.RutaPaquete;
import com.RapidExpress.model.enums.EstadoRuta;
import com.RapidExpress.model.enums.EstadoRutaPaquete;
import com.RapidExpress.service.PaqueteService;
import com.RapidExpress.service.RutaService;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.RutaView;

import java.time.LocalDate;
import java.util.List;

/**
 * Route controller - orchestrates route CRUD and lifecycle.
 * Connects directly to RutaService and PaqueteService to guarantee load capacity validation,
 * atomic multi-entity state transitions, and audit trails (Fixes Fallos 2 & 3, complies with SRP/DIP).
 */
public class RutaController {

    private final RutaService rutaService;
    private final PaqueteService paqueteService;
    private static final String DEFAULT_USER = "OPERADOR_CLI";

    public RutaController() {
        this(new RutaService(), new PaqueteService());
    }

    public RutaController(RutaService rutaService, PaqueteService paqueteService) {
        this.rutaService = rutaService;
        this.paqueteService = paqueteService;
    }

    public void run() {
        MenuRunner.run(
                RutaView::showMenu,
                RutaView::readMenuOption,
                this::crearRuta,                // 1
                this::listarTodos,              // 2
                this::buscarPorId,              // 3
                this::actualizarRuta,           // 4
                this::cambiarEstado,            // 5
                this::iniciarRuta,              // 6
                this::completarRuta,            // 7
                this::cancelarRuta,             // 8
                this::verPorEstado,             // 9
                this::verPorFecha,              // 10
                this::verPorVehiculo,           // 11
                this::verPorConductor,          // 12
                this::verEstadisticas,          // 13
                this::verPaquetesDeRuta,        // 14
                this::actualizarEntregaPaquete  // 15
        );
    }

    /**
     * Fallo 2 Solucionado: Permite seleccionar paquetes en bodega y valida
     * que el peso total no exceda la capacidad del vehículo.
     */
    private void crearRuta(int opt) {
        ConsoleUtil.printSubHeader("CREAR HOJA DE RUTA CON ASIGNACION DE CARGA");
        Ruta rutaData = RutaView.readNewRutaData();
        if (rutaData == null) return;

        List<Paquete> enBodega = paqueteService.listarEnBodega();
        if (enBodega.isEmpty()) {
            ConsoleUtil.printError("No hay paquetes en bodega disponibles para conformar la hoja de ruta.");
            return;
        }

        List<String> paquetesSeleccionados = RutaView.readPaquetesSeleccionados(enBodega);
        if (paquetesSeleccionados.isEmpty()) {
            ConsoleUtil.printError("Debe seleccionar al menos un paquete para crear la hoja de ruta.");
            return;
        }

        try {
            Ruta nuevaRuta = rutaService.crearHojaDeRuta(
                    rutaData.getFecha(),
                    rutaData.getVehiculoPlaca(),
                    rutaData.getConductorId(),
                    paquetesSeleccionados,
                    DEFAULT_USER
            );
            ConsoleUtil.printSuccess(String.format("Hoja de ruta %d creada exitosamente. Vehículo: %s, Conductor: %s, Paquetes: %d",
                    nuevaRuta.getIdRuta(), nuevaRuta.getVehiculoPlaca(), nuevaRuta.getConductorId(), paquetesSeleccionados.size()));
        } catch (CapacidadExcedidaException e) {
            ConsoleUtil.printError("CAPACIDAD EXCEDIDA: " + e.getMessage());
            ConsoleUtil.printError(String.format("Peso solicitado: %.2f kg | Capacidad máxima del vehículo: %.2f kg",
                    e.getPesoTotal(), e.getCapacidadMaxima()));
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtil.printError("Error inesperado: " + e.getMessage());
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE RUTAS");
        List<Ruta> rutas = rutaService.listarTodas();
        RutaView.showRutaList(rutas);
    }

    private void buscarPorId(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR RUTA POR ID");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta");
        try {
            Ruta ruta = rutaService.obtenerRuta(id);
            RutaView.showRutaDetail(ruta);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void actualizarRuta(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta a actualizar");
        try {
            Ruta ruta = rutaService.obtenerRuta(id);
            RutaView.showRutaDetail(ruta);
            Ruta actualizada = RutaView.readUpdatedRutaData(ruta);
            rutaService.actualizarRuta(actualizada, DEFAULT_USER);
            ConsoleUtil.printSuccess("Ruta actualizada exitosamente.");
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al actualizar: " + e.getMessage());
        }
    }

    private void cambiarEstado(int opt) {
        ConsoleUtil.printSubHeader("CAMBIAR ESTADO DE RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta");
        try {
            Ruta ruta = rutaService.obtenerRuta(id);
            RutaView.showRutaDetail(ruta);
            EstadoRuta nuevoEstado = RutaView.readNewEstado();
            rutaService.cambiarEstado(id, nuevoEstado, DEFAULT_USER);
            ConsoleUtil.printSuccess("Estado actualizado a: " + nuevoEstado);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al cambiar estado: " + e.getMessage());
        }
    }

    /**
     * Fallo 3 Solucionado: Pone en 'En Ruta' al vehículo y conductor, y en 'En Tránsito' a los paquetes.
     */
    private void iniciarRuta(int opt) {
        ConsoleUtil.printSubHeader("INICIAR RUTA (ACTUALIZACION MULTI-ENTIDAD)");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta a iniciar");
        try {
            rutaService.iniciarRuta(id, DEFAULT_USER);
            ConsoleUtil.printSuccess("Ruta " + id + " iniciada exitosamente.");
            ConsoleUtil.printInfo("Estado de vehículo y conductor actualizado a 'En_Ruta'. Paquetes actualizados a 'En_Transito'.");
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al iniciar ruta: " + e.getMessage());
        }
    }

    private void completarRuta(int opt) {
        ConsoleUtil.printSubHeader("COMPLETAR RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta a completar");
        try {
            rutaService.completarRuta(id, DEFAULT_USER);
            ConsoleUtil.printSuccess("Ruta " + id + " completada exitosamente.");
            ConsoleUtil.printInfo("Vehículo y conductor liberados a estado disponible.");
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al completar ruta: " + e.getMessage());
        }
    }

    private void cancelarRuta(int opt) {
        ConsoleUtil.printSubHeader("CANCELAR RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta a cancelar");
        try {
            rutaService.obtenerRuta(id);
            if (ConsoleUtil.readConfirmation("¿Confirma la cancelación de la ruta " + id + "?")) {
                rutaService.cancelarRuta(id, DEFAULT_USER);
                ConsoleUtil.printSuccess("Ruta " + id + " cancelada exitosamente.");
                ConsoleUtil.printInfo("Vehículo, conductor y paquetes no entregados restaurados.");
            }
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al cancelar ruta: " + e.getMessage());
        }
    }

    private void verPorEstado(int opt) {
        ConsoleUtil.printSubHeader("RUTAS POR ESTADO");
        EstadoRuta estado = RutaView.readNewEstado();
        List<Ruta> rutas = rutaService.listarPorEstado(estado);
        RutaView.showRutasByEstado(rutas, estado);
    }

    private void verPorFecha(int opt) {
        ConsoleUtil.printSubHeader("RUTAS POR FECHA");
        LocalDate fecha = ConsoleUtil.readDate("Fecha");
        List<Ruta> rutas = rutaService.listarPorFecha(fecha);
        RutaView.showRutasByFecha(rutas, fecha);
    }

    private void verPorVehiculo(int opt) {
        ConsoleUtil.printSubHeader("RUTAS POR VEHICULO");
        String placa = ConsoleUtil.readRequiredString("Placa del vehiculo").toUpperCase();
        List<Ruta> rutas = rutaService.listarPorVehiculo(placa);
        RutaView.showRutasByVehiculo(rutas, placa);
    }

    private void verPorConductor(int opt) {
        ConsoleUtil.printSubHeader("RUTAS POR CONDUCTOR");
        String conductor = ConsoleUtil.readRequiredString("ID del conductor");
        List<Ruta> rutas = rutaService.listarPorConductor(conductor);
        RutaView.showRutasByConductor(rutas, conductor);
    }

    private void verEstadisticas(int opt) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE RUTAS");
        int total = rutaService.contarRutas();
        int planificadas = rutaService.contarPorEstado(EstadoRuta.PLANIFICADA);
        int enCurso = rutaService.contarPorEstado(EstadoRuta.EN_CURSO);
        int completadas = rutaService.contarPorEstado(EstadoRuta.COMPLETADA);
        int canceladas = rutaService.contarPorEstado(EstadoRuta.CANCELADA);
        RutaView.showStats(total, planificadas, enCurso, completadas, canceladas);
    }

    private void verPaquetesDeRuta(int opt) {
        ConsoleUtil.printSubHeader("VER PAQUETES ASIGNADOS A UNA RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta");
        List<RutaPaquete> paquetes = rutaService.obtenerPaquetesDeRuta(id);
        RutaView.showRutaPaquetes(paquetes, id);
    }

    private void actualizarEntregaPaquete(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR ENTREGA DE PAQUETE EN RUTA ACTIVA");
        Long idRuta = RutaView.readIdRuta("Ingrese el ID de la ruta");
        String paqueteId = ConsoleUtil.readRequiredString("ID de seguimiento del paquete: ");
        EstadoRutaPaquete estado = RutaView.readEstadoEntregaPaquete();
        String obs = RutaView.readObservacionesEntrega();

        try {
            rutaService.actualizarEntregaPaquete(idRuta, paqueteId, estado, obs, DEFAULT_USER);
            ConsoleUtil.printSuccess(String.format("Entrega de paquete %s en ruta %d actualizada a %s", paqueteId, idRuta, estado));
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al actualizar entrega: " + e.getMessage());
        }
    }
}