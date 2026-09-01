package com.RapidExpress.controller;

import com.RapidExpress.dao.RutaDAO;
import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.dao.ConductorDAO;
import com.RapidExpress.model.entity.Ruta;
import com.RapidExpress.model.enums.EstadoRuta;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.RutaView;

import java.time.LocalDate;
import java.util.List;

/**
 * Route controller - orchestrates route CRUD and lifecycle.
 * Uses MenuRunner to eliminate boilerplate menu loop.
 */
public class RutaController {

    private final RutaDAO dao = new RutaDAO();
    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final ConductorDAO conductorDAO = new ConductorDAO();

    public void run() {
        MenuRunner.run(
                RutaView::showMenu,
                RutaView::readMenuOption,
                this::crearRuta,            // 1
                this::listarTodos,          // 2
                this::buscarPorId,          // 3
                this::actualizarRuta,       // 4
                this::cambiarEstado,        // 5
                this::iniciarRuta,          // 6
                this::completarRuta,        // 7
                this::cancelarRuta,         // 8
                this::verPorEstado,         // 9
                this::verPorFecha,          // 10
                this::verPorVehiculo,       // 11
                this::verPorConductor,      // 12
                this::verEstadisticas       // 13
        );
    }

    private void crearRuta(int opt) {
        ConsoleUtil.printSubHeader("CREAR NUEVA RUTA");
        Ruta ruta = RutaView.readNewRutaData();
        if (ruta == null) return;
        if (!vehiculoDAO.exists(ruta.getVehiculoPlaca())) {
            ConsoleUtil.printError("El vehiculo " + ruta.getVehiculoPlaca() + " no existe.");
            return;
        }
        if (!conductorDAO.exists(ruta.getConductorId())) {
            ConsoleUtil.printError("El conductor " + ruta.getConductorId() + " no existe.");
            return;
        }
        if (dao.create(ruta)) {
            ConsoleUtil.printSuccess("Ruta creada exitosamente. ID: " + ruta.getIdRuta());
        } else {
            ConsoleUtil.printError("No se pudo crear la ruta.");
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE RUTAS");
        List<Ruta> rutas = dao.findAll();
        RutaView.showRutaList(rutas);
    }

    private void buscarPorId(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR RUTA POR ID");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta");
        dao.findById(id).ifPresentOrElse(
                RutaView::showRutaDetail,
                () -> ConsoleUtil.printError("Ruta no encontrada.")
        );
    }

    private void actualizarRuta(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta a actualizar");
        dao.findById(id).ifPresentOrElse(ruta -> {
            RutaView.showRutaDetail(ruta);
            Ruta actualizada = RutaView.readUpdatedRutaData(ruta);
            if (dao.update(actualizada)) {
                ConsoleUtil.printSuccess("Ruta actualizada exitosamente.");
            } else {
                ConsoleUtil.printError("No se pudo actualizar la ruta.");
            }
        }, () -> ConsoleUtil.printError("Ruta no encontrada."));
    }

    private void cambiarEstado(int opt) {
        ConsoleUtil.printSubHeader("CAMBIAR ESTADO DE RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta");
        dao.findById(id).ifPresentOrElse(ruta -> {
            RutaView.showRutaDetail(ruta);
            EstadoRuta nuevoEstado = RutaView.readNewEstado();
            if (dao.updateEstado(id, nuevoEstado)) {
                ConsoleUtil.printSuccess("Estado actualizado a: " + nuevoEstado);
            } else {
                ConsoleUtil.printError("No se pudo actualizar el estado.");
            }
        }, () -> ConsoleUtil.printError("Ruta no encontrada."));
    }

    private void iniciarRuta(int opt) {
        ConsoleUtil.printSubHeader("INICIAR RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta a iniciar");
        dao.findById(id).ifPresentOrElse(ruta -> {
            if (dao.iniciarRuta(id)) {
                ConsoleUtil.printSuccess("Ruta " + id + " iniciada.");
            } else {
                ConsoleUtil.printError("No se pudo iniciar la ruta.");
            }
        }, () -> ConsoleUtil.printError("Ruta no encontrada."));
    }

    private void completarRuta(int opt) {
        ConsoleUtil.printSubHeader("COMPLETAR RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta a completar");
        dao.findById(id).ifPresentOrElse(ruta -> {
            if (dao.completarRuta(id)) {
                ConsoleUtil.printSuccess("Ruta " + id + " completada.");
            } else {
                ConsoleUtil.printError("No se pudo completar la ruta.");
            }
        }, () -> ConsoleUtil.printError("Ruta no encontrada."));
    }

    private void cancelarRuta(int opt) {
        ConsoleUtil.printSubHeader("CANCELAR RUTA");
        Long id = RutaView.readIdRuta("Ingrese el ID de la ruta a cancelar");
        dao.findById(id).ifPresentOrElse(ruta -> {
            if (ConsoleUtil.readConfirmation("Cancelar ruta " + id + "?")) {
                if (dao.cancelarRuta(id)) {
                    ConsoleUtil.printSuccess("Ruta " + id + " cancelada.");
                } else {
                    ConsoleUtil.printError("No se pudo cancelar la ruta.");
                }
            }
        }, () -> ConsoleUtil.printError("Ruta no encontrada."));
    }

    private void verPorEstado(int opt) {
        ConsoleUtil.printSubHeader("RUTAS POR ESTADO");
        EstadoRuta estado = RutaView.readNewEstado();
        List<Ruta> rutas = dao.findByEstado(estado);
        RutaView.showRutasByEstado(rutas, estado);
    }

    private void verPorFecha(int opt) {
        ConsoleUtil.printSubHeader("RUTAS POR FECHA");
        LocalDate fecha = ConsoleUtil.readDate("Fecha");
        List<Ruta> rutas = dao.findByFecha(fecha);
        RutaView.showRutasByFecha(rutas, fecha);
    }

    private void verPorVehiculo(int opt) {
        ConsoleUtil.printSubHeader("RUTAS POR VEHICULO");
        String placa = ConsoleUtil.readRequiredString("Placa del vehiculo").toUpperCase();
        List<Ruta> rutas = dao.findByVehiculo(placa);
        RutaView.showRutasByVehiculo(rutas, placa);
    }

    private void verPorConductor(int opt) {
        ConsoleUtil.printSubHeader("RUTAS POR CONDUCTOR");
        String conductor = ConsoleUtil.readRequiredString("ID del conductor");
        List<Ruta> rutas = dao.findByConductor(conductor);
        RutaView.showRutasByConductor(rutas, conductor);
    }

    private void verEstadisticas(int opt) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE RUTAS");
        int total = dao.count();
        int planificadas = dao.countByEstado(EstadoRuta.PLANIFICADA);
        int enCurso = dao.countByEstado(EstadoRuta.EN_CURSO);
        int completadas = dao.countByEstado(EstadoRuta.COMPLETADA);
        int canceladas = dao.countByEstado(EstadoRuta.CANCELADA);
        RutaView.showStats(total, planificadas, enCurso, completadas, canceladas);
    }
}