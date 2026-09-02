package com.RapidExpress.controller;

import com.RapidExpress.dao.PaqueteDAO;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.enums.EstadoPaquete;
import com.RapidExpress.view.ConsoleUtil;
import com.RapidExpress.view.PaqueteView;

import java.util.List;

/**
 * Package controller - orchestrates package CRUD, state changes, and search.
 * Uses MenuRunner to eliminate boilerplate menu loop.
 */
public class PaqueteController {

    private final PaqueteDAO dao = new PaqueteDAO();

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

    private void crearPaquete(int opt) {
        ConsoleUtil.printSubHeader("REGISTRAR NUEVO PAQUETE");
        Paquete paquete = PaqueteView.readNewPaqueteData();
        if (paquete != null) {
            if (dao.create(paquete)) {
                ConsoleUtil.printSuccess("Paquete registrado exitosamente. ID: " + paquete.getIdSeguimiento());
            } else {
                ConsoleUtil.printError("No se pudo registrar el paquete.");
            }
        }
    }

    private void listarTodos(int opt) {
        ConsoleUtil.printSubHeader("LISTA DE PAQUETES");
        List<Paquete> paquetes = dao.findAll();
        PaqueteView.showPaqueteList(paquetes);
    }

    private void buscarPorId(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR PAQUETE POR ID DE SEGUIMIENTO");
        String id = PaqueteView.readIdSeguimiento("Ingrese el ID de seguimiento");
        dao.findById(id).ifPresentOrElse(
                PaqueteView::showPaqueteDetail,
                () -> ConsoleUtil.printError("Paquete no encontrado.")
        );
    }

    private void actualizarPaquete(int opt) {
        ConsoleUtil.printSubHeader("ACTUALIZAR PAQUETE");
        String id = PaqueteView.readIdSeguimiento("Ingrese el ID de seguimiento del paquete a actualizar");
        dao.findById(id).ifPresentOrElse(paquete -> {
            Paquete actualizado = PaqueteView.readUpdatedPaqueteData(paquete);
            if (dao.update(actualizado)) {
                ConsoleUtil.printSuccess("Paquete actualizado exitosamente.");
            } else {
                ConsoleUtil.printError("No se pudo actualizar el paquete.");
            }
        }, () -> ConsoleUtil.printError("Paquete no encontrado."));
    }

    private void cambiarEstado(int opt) {
        ConsoleUtil.printSubHeader("CAMBIAR ESTADO DEL PAQUETE");
        String id = PaqueteView.readIdSeguimiento("Ingrese el ID de seguimiento");
        dao.findById(id).ifPresentOrElse(paquete -> {
            PaqueteView.showPaqueteDetail(paquete);
            EstadoPaquete nuevoEstado = PaqueteView.readNewEstado();
            if (dao.updateEstado(id, nuevoEstado)) {
                ConsoleUtil.printSuccess("Estado actualizado a: " + nuevoEstado);
            } else {
                ConsoleUtil.printError("No se pudo actualizar el estado.");
            }
        }, () -> ConsoleUtil.printError("Paquete no encontrado."));
    }

    private void eliminarPaquete(int opt) {
        ConsoleUtil.printSubHeader("ELIMINAR PAQUETE");
        String id = PaqueteView.readIdSeguimiento("Ingrese el ID de seguimiento del paquete a eliminar");
        dao.findById(id).ifPresentOrElse(paquete -> {
            PaqueteView.showPaqueteDetail(paquete);
            if (PaqueteView.confirmDelete(id)) {
                if (dao.delete(id)) {
                    ConsoleUtil.printSuccess("Paquete eliminado exitosamente.");
                } else {
                    ConsoleUtil.printError("No se pudo eliminar el paquete.");
                }
            } else {
                ConsoleUtil.printInfo("Operacion cancelada.");
            }
        }, () -> ConsoleUtil.printError("Paquete no encontrado."));
    }

    private void verPorEstado(int opt) {
        ConsoleUtil.printSubHeader("PAQUETES POR ESTADO");
        EstadoPaquete estado = PaqueteView.readNewEstado();
        List<Paquete> paquetes = dao.findByEstado(estado);
        PaqueteView.showPaquetesByEstado(paquetes, estado);
    }

    private void buscarPorRemitente(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR PAQUETES POR REMITENTE");
        String remitente = PaqueteView.readRemitenteNombre();
        List<Paquete> paquetes = dao.findByRemitente(remitente);
        PaqueteView.showPaquetesByRemitente(paquetes, remitente);
    }

    private void buscarPorDestinatario(int opt) {
        ConsoleUtil.printSubHeader("BUSCAR PAQUETES POR DESTINATARIO");
        String destinatario = PaqueteView.readDestinatarioNombre();
        List<Paquete> paquetes = dao.findByDestinatario(destinatario);
        PaqueteView.showPaquetesByDestinatario(paquetes, destinatario);
    }

    private void verEnBodega(int opt) {
        ConsoleUtil.printSubHeader("PAQUETES EN BODEGA (DISPONIBLES PARA RUTA)");
        List<Paquete> paquetes = dao.findEnBodega();
        PaqueteView.showPaqueteList(paquetes);
    }
}
