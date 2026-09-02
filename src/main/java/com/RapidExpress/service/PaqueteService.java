package com.RapidExpress.service;

import com.RapidExpress.dao.PaqueteDAO;
import com.RapidExpress.exception.EntidadDuplicadaException;
import com.RapidExpress.exception.EntidadNoEncontradaException;
import com.RapidExpress.exception.EstadoInvalidoException;
import com.RapidExpress.exception.ValidacionException;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.enums.EstadoPaquete;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Capa de servicios y lógica de negocio para la gestión y trazabilidad de paquetes y envíos.
 */
public class PaqueteService {

    private final PaqueteDAO paqueteDAO;
    private final AuditoriaService auditoriaService;

    public PaqueteService() {
        this.paqueteDAO = new PaqueteDAO();
        this.auditoriaService = new AuditoriaService();
    }

    public PaqueteService(PaqueteDAO paqueteDAO, AuditoriaService auditoriaService) {
        this.paqueteDAO = paqueteDAO;
        this.auditoriaService = auditoriaService;
    }

    public String registrarPaquete(Paquete paquete, String usuario) throws ValidacionException, EntidadDuplicadaException {
        if (paquete.getIdSeguimiento() == null || paquete.getIdSeguimiento().trim().isEmpty()) {
            paquete.setIdSeguimiento(generarIdSeguimiento());
        }

        validarPaquete(paquete);

        if (paqueteDAO.exists(paquete.getIdSeguimiento())) {
            throw new EntidadDuplicadaException("Paquete", paquete.getIdSeguimiento());
        }

        boolean creado = paqueteDAO.create(paquete);
        if (!creado) {
            throw new ValidacionException("No se pudo registrar el paquete en la base de datos.");
        }

        auditoriaService.registrar("CREATE", "paquete", paquete.getIdSeguimiento(), usuario,
                String.format("Desc: %s, Peso: %.2f kg, Destino: %s, Destinatario: %s",
                        paquete.getDescripcion(), paquete.getPesoKg(), paquete.getDirDestino(), paquete.getDestinatarioNombre()));

        return paquete.getIdSeguimiento();
    }

    public void actualizarPaquete(Paquete paquete, String usuario) throws EntidadNoEncontradaException, ValidacionException {
        validarPaquete(paquete);

        Paquete actual = obtenerPaquete(paquete.getIdSeguimiento());
        boolean actualizado = paqueteDAO.update(paquete);
        if (!actualizado) {
            throw new ValidacionException("No se pudo actualizar el paquete.");
        }

        auditoriaService.registrar("UPDATE", "paquete", paquete.getIdSeguimiento(), usuario,
                String.format("Actualizado: %s, Peso: %.2f kg", paquete.getDescripcion(), paquete.getPesoKg()));
    }

    public void cambiarEstado(String idSeguimiento, EstadoPaquete nuevoEstado, String usuario)
            throws EntidadNoEncontradaException, ValidacionException {
        Paquete paquete = obtenerPaquete(idSeguimiento);

        boolean actualizado = paqueteDAO.updateEstado(idSeguimiento, nuevoEstado);
        if (!actualizado) {
            throw new ValidacionException("No se pudo actualizar el estado del paquete.");
        }

        auditoriaService.registrar("STATE_CHANGE", "paquete", idSeguimiento, usuario,
                String.format("Estado anterior: %s -> Nuevo: %s", paquete.getEstado(), nuevoEstado));
    }

    public void eliminarPaquete(String idSeguimiento, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {
        Paquete paquete = obtenerPaquete(idSeguimiento);

        if (paquete.isEnTransito() || paquete.isAsignadoRuta()) {
            throw new EstadoInvalidoException("No se puede eliminar un paquete que está asignado a una ruta o en tránsito.");
        }

        boolean eliminado = paqueteDAO.delete(idSeguimiento);
        if (!eliminado) {
            throw new ValidacionException("No se pudo eliminar el paquete. Puede tener historial de rutas asociado.");
        }

        auditoriaService.registrar("DELETE", "paquete", idSeguimiento, usuario, "Paquete eliminado.");
    }

    public Paquete obtenerPaquete(String idSeguimiento) throws EntidadNoEncontradaException {
        if (idSeguimiento == null || idSeguimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de seguimiento no puede ser nulo o vacío.");
        }
        return paqueteDAO.findById(idSeguimiento.trim())
                .orElseThrow(() -> new EntidadNoEncontradaException("Paquete", idSeguimiento));
    }

    public List<Paquete> listarTodos() {
        return paqueteDAO.findAll();
    }

    public List<Paquete> listarEnBodega() {
        return paqueteDAO.findEnBodega();
    }

    public List<Paquete> listarEnTransito() {
        return paqueteDAO.findEnTransito();
    }

    public List<Paquete> listarEntregados() {
        return paqueteDAO.findEntregados();
    }

    public List<Paquete> buscarPorRemitente(String remitente) {
        return paqueteDAO.findByRemitente(remitente);
    }

    public List<Paquete> buscarPorDestinatario(String destinatario) {
        return paqueteDAO.findByDestinatario(destinatario);
    }

    private String generarIdSeguimiento() {
        return "PKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void validarPaquete(Paquete p) throws ValidacionException {
        if (p == null) {
            throw new ValidacionException("El paquete no puede ser nulo.");
        }
        if (p.getDescripcion() == null || p.getDescripcion().trim().isEmpty()) {
            throw new ValidacionException("La descripción del contenido es obligatoria.");
        }
        if (p.getPesoKg() == null || p.getPesoKg().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidacionException("El peso en kg debe ser mayor a 0.");
        }
        if (p.getDimensiones() == null || p.getDimensiones().trim().isEmpty()) {
            throw new ValidacionException("Las dimensiones son obligatorias.");
        }
        if (p.getDirOrigen() == null || p.getDirOrigen().trim().isEmpty()) {
            throw new ValidacionException("La dirección de origen es obligatoria.");
        }
        if (p.getDirDestino() == null || p.getDirDestino().trim().isEmpty()) {
            throw new ValidacionException("La dirección de destino es obligatoria.");
        }
        if (p.getRemitenteNombre() == null || p.getRemitenteNombre().trim().isEmpty()) {
            throw new ValidacionException("El nombre del remitente es obligatorio.");
        }
        if (p.getRemitenteContacto() == null || p.getRemitenteContacto().trim().isEmpty()) {
            throw new ValidacionException("El contacto del remitente es obligatorio.");
        }
        if (p.getDestinatarioNombre() == null || p.getDestinatarioNombre().trim().isEmpty()) {
            throw new ValidacionException("El nombre del destinatario es obligatorio.");
        }
        if (p.getDestinatarioContacto() == null || p.getDestinatarioContacto().trim().isEmpty()) {
            throw new ValidacionException("El contacto del destinatario es obligatorio.");
        }
    }
}
