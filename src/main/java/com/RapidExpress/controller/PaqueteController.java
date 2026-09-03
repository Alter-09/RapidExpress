package com.RapidExpress.controller;

import com.RapidExpress.exception.RapidExpressException;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.enums.EstadoPaquete;
import com.RapidExpress.service.PaqueteService;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.PaqueteView;

import java.util.List;

/**
 * Package controller - orchestrates package CRUD, state changes, and search.
 * Connects directly to PaqueteService to apply domain validation, UUID generation,
 * and centralized audit logging (Fixes Fallo 1 and complies with SRP/DIP).
 */
public class PaqueteController {

    private final PaqueteService paqueteService;
    private static final String DEFAULT_USER = "OPERADOR_CLI";

    public PaqueteController() {
        this(new PaqueteService());
    }

    public PaqueteController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    public void run() {
        MenuRunner.run(
                PaqueteView::showMenu,
                PaqueteView::readMenuOption,
                this::crearPaquete,             // 1
                this::listarTodos,              // 2
                this::buscarPorId,              // 3
                this::actualizarPaquete,        // 4
                this::cambiarEstado,            // 5
                this::eliminarPaquete,          // 6
                this::verPorEstado,             // 7
                this::buscarPorRemitente,       // 8
                this::buscarPorDestinatario,    // 9
                this::verEnBodega               // 10
        );
    }

    /**
     * Fallo 1 Solucionado: Delega el registro al servicio para generar el UUID
     * de seguimiento y auditar la operación.
     */
    private void crearPaquete(int opt) {
        ConsoleUtil.printSubHeader("REGISTRAR NUEVO PAQUETE");
        Paquete paquete = PaqueteView.readNewPaqueteData();
        if (paquete != null) {
            try {
                String idGenerado = paqueteService.registrarPaquete(paquete, DEFAULT_USER);
                ConsoleUtil.printSuccess("Paquete registrado exitosamente. ID de seguimiento: " + idGenerado);
            } catch (RapidExpressException e) {
                ConsoleUtil.printError("Error al registrar paquete: " + e.getMessage());
            } catch (Exception e) {
                ConsoleUtil.printError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE PAQUETES");
        List<Paquete> paquetes = paqueteService.listarTodos();
        PaqueteView.showPaqueteList(paquetes);
    }

    private void buscarPorId(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR PAQUETE POR ID DE SEGUIMIENTO");
        String id = PaqueteView.readIdSeguimiento("Ingrese el ID de seguimiento");
        try {
            Paquete paquete = paqueteService.obtenerPaquete(id);
            PaqueteView.showPaqueteDetail(paquete);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void actualizarPaquete(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR PAQUETE");
        String id = PaqueteView.readIdSeguimiento("Ingrese el ID de seguimiento del paquete a actualizar");
        try {
            Paquete actual = paqueteService.obtenerPaquete(id);
            Paquete actualizado = PaqueteView.readUpdatedPaqueteData(actual);
            paqueteService.actualizarPaquete(actualizado, DEFAULT_USER);
            ConsoleUtil.printSuccess("Paquete actualizado exitosamente.");
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al actualizar paquete: " + e.getMessage());
        }
    }

    private void cambiarEstado(int opt) {
        ConsoleUtil.printSubHeader("CAMBIAR ESTADO DEL PAQUETE");
        String id = PaqueteView.readIdSeguimiento("Ingrese el ID de seguimiento");
        try {
            Paquete paquete = paqueteService.obtenerPaquete(id);
            PaqueteView.showPaqueteDetail(paquete);
            EstadoPaquete nuevoEstado = PaqueteView.readNewEstado();
            paqueteService.cambiarEstado(id, nuevoEstado, DEFAULT_USER);
            ConsoleUtil.printSuccess("Estado actualizado a: " + nuevoEstado);
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al cambiar estado: " + e.getMessage());
        }
    }

    private void eliminarPaquete(int opt) {
        ConsoleUtil.printSubHeader("ELIMINAR PAQUETE");
        String id = PaqueteView.readIdSeguimiento("Ingrese el ID de seguimiento del paquete a eliminar");
        try {
            Paquete paquete = paqueteService.obtenerPaquete(id);
            PaqueteView.showPaqueteDetail(paquete);
            if (PaqueteView.confirmDelete(id)) {
                paqueteService.eliminarPaquete(id, DEFAULT_USER);
                ConsoleUtil.printSuccess("Paquete eliminado exitosamente.");
            } else {
                ConsoleUtil.printInfo("Operacion cancelada.");
            }
        } catch (RapidExpressException e) {
            ConsoleUtil.printError("Error al eliminar paquete: " + e.getMessage());
        }
    }

    private void verPorEstado(int opt) {
        ConsoleUtil.printSubHeader("PAQUETES POR ESTADO");
        EstadoPaquete estado = PaqueteView.readNewEstado();
        List<Paquete> paquetes = paqueteService.listarPorEstado(estado);
        PaqueteView.showPaquetesByEstado(paquetes, estado);
    }

    private void buscarPorRemitente(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR PAQUETES POR REMITENTE");
        String remitente = PaqueteView.readRemitenteNombre();
        List<Paquete> paquetes = paqueteService.buscarPorRemitente(remitente);
        PaqueteView.showPaquetesByRemitente(paquetes, remitente);
    }

    private void buscarPorDestinatario(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR PAQUETES POR DESTINATARIO");
        String destinatario = PaqueteView.readDestinatarioNombre();
        List<Paquete> paquetes = paqueteService.buscarPorDestinatario(destinatario);
        PaqueteView.showPaquetesByDestinatario(paquetes, destinatario);
    }

    private void verEnBodega(int opt) {
        ConsoleUtil.printSubHeader("PAQUETES EN BODEGA (DISPONIBLES PARA RUTA)");
        List<Paquete> paquetes = paqueteService.listarEnBodega();
        PaqueteView.showPaqueteList(paquetes);
    }
}
