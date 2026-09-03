package com.RapidExpress.service;

import com.RapidExpress.dao.AuditoriaDAO;
import com.RapidExpress.model.entity.Auditoria;
import com.RapidExpress.util.FileLogUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión y persistencia de eventos de auditoría (MySQL + Archivo plano).
 */
public class AuditoriaService {

    private final AuditoriaDAO auditoriaDAO;

    public AuditoriaService() {
        this.auditoriaDAO = new AuditoriaDAO();
    }

    public AuditoriaService(AuditoriaDAO auditoriaDAO) {
        this.auditoriaDAO = auditoriaDAO;
    }

    /**
     * Registra un evento de auditoría de forma dual: Base de datos y archivo logs/auditoria.log.
     */
    public void registrar(String operacion, String entidad, String entidadId, String usuario, String detalles) {
        // 1. Persistencia en Base de Datos
        auditoriaDAO.log(operacion, entidad, entidadId, usuario, detalles, "CLI-Local");

        // 2. Persistencia en Archivo de Texto Plano
        FileLogUtil.appendAuditLog(operacion, entidad, entidadId, usuario, detalles);
    }

    public List<Auditoria> listarTodas() {
        return auditoriaDAO.findAll();
    }

    public List<Auditoria> listarUltimas(int limite) {
        return auditoriaDAO.findUltimas(limite);
    }

    public List<Auditoria> buscarPorEntidad(String entidad) {
        return auditoriaDAO.findByEntidad(entidad);
    }

    public List<Auditoria> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        return auditoriaDAO.findByRangoFechas(inicio, fin);
    }

    public List<String> leerLogsDeArchivo() {
        return FileLogUtil.readAuditLogs();
    }

    public Optional<Auditoria> obtenerPorId(Long id) {
        return auditoriaDAO.findById(id);
    }
}
