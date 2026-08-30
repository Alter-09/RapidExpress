package com.RapidExpress.service;

import com.RapidExpress.dao.ConductorDAO;
import com.RapidExpress.dao.PaqueteDAO;
import com.RapidExpress.dao.RutaDAO;
import com.RapidExpress.dao.RutaPaqueteDAO;
import com.RapidExpress.dao.VehiculoDAO;
import com.RapidExpress.exception.CapacidadExcedidaException;
import com.RapidExpress.exception.EntidadNoEncontradaException;
import com.RapidExpress.exception.EstadoInvalidoException;
import com.RapidExpress.exception.ValidacionException;
import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.entity.Ruta;
import com.RapidExpress.model.entity.RutaPaquete;
import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.EstadoPaquete;
import com.RapidExpress.model.enums.EstadoRuta;
import com.RapidExpress.model.enums.EstadoRutaPaquete;
import com.RapidExpress.model.enums.EstadoVehiculo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de servicios y lógica de negocio principal para la planificación y seguimiento de rutas logísticas.
 */
public class RutaService {

    private final RutaDAO rutaDAO;
    private final RutaPaqueteDAO rutaPaqueteDAO;
    private final VehiculoDAO vehiculoDAO;
    private final ConductorDAO conductorDAO;
    private final PaqueteDAO paqueteDAO;
    private final AuditoriaService auditoriaService;

    public RutaService() {
        this.rutaDAO = new RutaDAO();
        this.rutaPaqueteDAO = new RutaPaqueteDAO();
        this.vehiculoDAO = new VehiculoDAO();
        this.conductorDAO = new ConductorDAO();
        this.paqueteDAO = new PaqueteDAO();
        this.auditoriaService = new AuditoriaService();
    }

    public RutaService(RutaDAO rutaDAO, RutaPaqueteDAO rutaPaqueteDAO, VehiculoDAO vehiculoDAO,
                       ConductorDAO conductorDAO, PaqueteDAO paqueteDAO, AuditoriaService auditoriaService) {
        this.rutaDAO = rutaDAO;
        this.rutaPaqueteDAO = rutaPaqueteDAO;
        this.vehiculoDAO = vehiculoDAO;
        this.conductorDAO = conductorDAO;
        this.paqueteDAO = paqueteDAO;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Crea una hoja de ruta diaria validando estrictamente la capacidad máxima de carga del vehículo.
     */
    public Ruta crearHojaDeRuta(LocalDate fecha, String vehiculoPlaca, String conductorId,
                                List<String> paquetesIds, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, CapacidadExcedidaException, ValidacionException {

        if (fecha == null) {
            fecha = LocalDate.now();
        }
        if (paquetesIds == null || paquetesIds.isEmpty()) {
            throw new ValidacionException("Debe seleccionar al menos un paquete para conformar la hoja de ruta.");
        }

        // 1. Validar Vehículo
        Vehiculo vehiculo = vehiculoDAO.findById(vehiculoPlaca.toUpperCase().trim())
                .orElseThrow(() -> new EntidadNoEncontradaException("Vehículo", vehiculoPlaca));

        if (!vehiculo.isDisponible()) {
            throw new EstadoInvalidoException(String.format("El vehículo %s no está disponible (Estado: %s).",
                    vehiculoPlaca, vehiculo.getEstado()));
        }

        // 2. Validar Conductor
        Conductor conductor = conductorDAO.findById(conductorId.trim())
                .orElseThrow(() -> new EntidadNoEncontradaException("Conductor", conductorId));

        if (!conductor.isActivo()) {
            throw new EstadoInvalidoException(String.format("El conductor %s no está activo (Estado: %s).",
                    conductor.getNombreCompleto(), conductor.getEstado()));
        }

        // 3. Validar Paquetes y Calcular Peso Total
        BigDecimal pesoTotal = BigDecimal.ZERO;
        List<Paquete> paquetesValidados = new ArrayList<>();

        for (String idPkg : paquetesIds) {
            Paquete p = paqueteDAO.findById(idPkg.trim())
                    .orElseThrow(() -> new EntidadNoEncontradaException("Paquete", idPkg));

            if (!p.isEnBodega()) {
                throw new EstadoInvalidoException(String.format("El paquete %s no está 'En Bodega' (Estado: %s).",
                        idPkg, p.getEstado()));
            }

            pesoTotal = pesoTotal.add(p.getPesoKg());
            paquetesValidados.add(p);
        }

        // 4. Validación de Capacidad Máxima del Vehículo
        if (pesoTotal.compareTo(vehiculo.getCapacidadMaxKg()) > 0) {
            throw new CapacidadExcedidaException(
                    "El peso total de los paquetes seleccionados excede la capacidad máxima de carga del vehículo.",
                    pesoTotal, vehiculo.getCapacidadMaxKg());
        }

        // 5. Crear la Ruta
        Ruta nuevaRuta = Ruta.builder()
                .fecha(fecha)
                .vehiculoPlaca(vehiculo.getPlaca())
                .conductorId(conductor.getNumIdentificacion())
                .estado(EstadoRuta.PLANIFICADA)
                .build();

        boolean rutaCreada = rutaDAO.create(nuevaRuta);
        if (!rutaCreada || nuevaRuta.getIdRuta() == null) {
            throw new ValidacionException("No se pudo crear el registro de la ruta en la base de datos.");
        }

        // 6. Asignar Paquetes a la Ruta
        short orden = 1;
        for (Paquete pkg : paquetesValidados) {
            RutaPaquete rp = RutaPaquete.builder()
                    .rutaId(nuevaRuta.getIdRuta())
                    .paqueteId(pkg.getIdSeguimiento())
                    .estadoPaquete(EstadoRutaPaquete.ASIGNADO)
                    .ordenEntrega(orden++)
                    .observaciones("Asignado en planificación")
                    .build();

            rutaPaqueteDAO.create(rp);
            paqueteDAO.updateEstado(pkg.getIdSeguimiento(), EstadoPaquete.ASIGNADO_RUTA);
        }

        // 7. Auditoría
        auditoriaService.registrar("CREATE_ROUTE", "ruta", String.valueOf(nuevaRuta.getIdRuta()), usuario,
                String.format("Ruta ID: %d creada. Vehículo: %s, Conductor: %s, Paquetes: %d, Peso total: %.2f/%.2f kg",
                        nuevaRuta.getIdRuta(), vehiculo.getPlaca(), conductor.getNumIdentificacion(),
                        paquetesValidados.size(), pesoTotal, vehiculo.getCapacidadMaxKg()));

        return nuevaRuta;
    }

    /**
     * Inicia una ruta cambiando atómicamente el estado del vehículo, conductor y paquetes.
     */
    public void iniciarRuta(Long idRuta, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {

        Ruta ruta = obtenerRuta(idRuta);

        if (!ruta.isPlanificada()) {
            throw new EstadoInvalidoException(String.format("Solo se pueden iniciar rutas Planificadas (Estado actual: %s).",
                    ruta.getEstado()));
        }

        // 1. Actualizar estado de la ruta
        rutaDAO.iniciarRuta(idRuta);

        // 2. Actualizar estado de vehículo y conductor
        vehiculoDAO.updateEstado(ruta.getVehiculoPlaca(), EstadoVehiculo.EN_RUTA);
        conductorDAO.updateEstado(ruta.getConductorId(), EstadoConductor.EN_RUTA);

        // 3. Actualizar estado de los paquetes asociados a En Tránsito
        List<RutaPaquete> paquetesRuta = rutaPaqueteDAO.findByRutaId(idRuta);
        for (RutaPaquete rp : paquetesRuta) {
            rutaPaqueteDAO.updateEstado(idRuta, rp.getPaqueteId(), EstadoRutaPaquete.EN_TRANSITO);
            paqueteDAO.updateEstado(rp.getPaqueteId(), EstadoPaquete.EN_TRANSITO);
        }

        auditoriaService.registrar("START_ROUTE", "ruta", String.valueOf(idRuta), usuario,
                String.format("Ruta %d iniciada. Vehículo %s y Conductor %s puestos En Ruta.",
                        idRuta, ruta.getVehiculoPlaca(), ruta.getConductorId()));
    }

    /**
     * Actualiza el estado de entrega de un paquete individual durante la ejecución de la ruta.
     */
    public void actualizarEntregaPaquete(Long idRuta, String paqueteId, EstadoRutaPaquete nuevoEstado,
                                         String observaciones, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException, ValidacionException {

        Ruta ruta = obtenerRuta(idRuta);
        if (!ruta.isEnCurso()) {
            throw new EstadoInvalidoException("Solo se pueden actualizar entregas en rutas que estén En Curso.");
        }

        if (!rutaPaqueteDAO.exists(idRuta, paqueteId)) {
            throw new EntidadNoEncontradaException(String.format("El paquete %s no pertenece a la ruta %d", paqueteId, idRuta));
        }

        // Actualizar en ruta_paquete
        switch (nuevoEstado) {
            case ENTREGADO:
                rutaPaqueteDAO.marcarEntregado(idRuta, paqueteId, observaciones);
                paqueteDAO.updateEstado(paqueteId, EstadoPaquete.ENTREGADO);
                break;
            case DEVUELTO:
                rutaPaqueteDAO.marcarDevuelto(idRuta, paqueteId, observaciones);
                paqueteDAO.updateEstado(paqueteId, EstadoPaquete.DEVUELTO);
                break;
            case NO_ENTREGADO:
                rutaPaqueteDAO.marcarNoEntregado(idRuta, paqueteId, observaciones);
                paqueteDAO.updateEstado(paqueteId, EstadoPaquete.EN_BODEGA);
                break;
            default:
                rutaPaqueteDAO.updateEstado(idRuta, paqueteId, nuevoEstado);
                break;
        }

        auditoriaService.registrar("UPDATE_PACKAGE_DELIVERY", "ruta_paquete", idRuta + ":" + paqueteId, usuario,
                String.format("Paquete: %s en Ruta: %d -> Estado: %s. Obs: %s", paqueteId, idRuta, nuevoEstado, observaciones));
    }

    /**
     * Completa una ruta y libera el vehículo y conductor para nuevas asignaciones.
     */
    public void completarRuta(Long idRuta, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException {

        Ruta ruta = obtenerRuta(idRuta);

        if (!ruta.isEnCurso()) {
            throw new EstadoInvalidoException(String.format("Solo se pueden completar rutas En Curso (Estado actual: %s).",
                    ruta.getEstado()));
        }

        // Finalizar ruta
        rutaDAO.completarRuta(idRuta);

        // Liberar vehículo y conductor
        vehiculoDAO.updateEstado(ruta.getVehiculoPlaca(), EstadoVehiculo.DISPONIBLE);
        conductorDAO.updateEstado(ruta.getConductorId(), EstadoConductor.ACTIVO);

        auditoriaService.registrar("END_ROUTE", "ruta", String.valueOf(idRuta), usuario,
                String.format("Ruta %d completada exitosamente. Vehículo %s y conductor %s liberados.",
                        idRuta, ruta.getVehiculoPlaca(), ruta.getConductorId()));
    }

    /**
     * Cancela una ruta y restablece los paquetes a 'En Bodega'.
     */
    public void cancelarRuta(Long idRuta, String usuario)
            throws EntidadNoEncontradaException, EstadoInvalidoException {

        Ruta ruta = obtenerRuta(idRuta);

        if (ruta.isCompletada()) {
            throw new EstadoInvalidoException("No se puede cancelar una ruta que ya ha sido completada.");
        }

        rutaDAO.cancelarRuta(idRuta);

        // Restaurar estado de vehículos y conductores si estaban en ruta
        if (ruta.isEnCurso()) {
            vehiculoDAO.updateEstado(ruta.getVehiculoPlaca(), EstadoVehiculo.DISPONIBLE);
            conductorDAO.updateEstado(ruta.getConductorId(), EstadoConductor.ACTIVO);
        }

        // Regresar paquetes no entregados a bodega
        List<RutaPaquete> paquetes = rutaPaqueteDAO.findByRutaId(idRuta);
        for (RutaPaquete rp : paquetes) {
            if (rp.getEstadoPaquete() != EstadoRutaPaquete.ENTREGADO) {
                paqueteDAO.updateEstado(rp.getPaqueteId(), EstadoPaquete.EN_BODEGA);
            }
        }

        auditoriaService.registrar("CANCEL_ROUTE", "ruta", String.valueOf(idRuta), usuario,
                String.format("Ruta %d cancelada.", idRuta));
    }

    public Ruta obtenerRuta(Long idRuta) throws EntidadNoEncontradaException {
        if (idRuta == null || idRuta <= 0) {
            throw new IllegalArgumentException("El ID de ruta debe ser válido.");
        }
        return rutaDAO.findById(idRuta)
                .orElseThrow(() -> new EntidadNoEncontradaException("Ruta", String.valueOf(idRuta)));
    }

    public List<Ruta> listarTodas() {
        return rutaDAO.findAll();
    }

    public List<Ruta> listarEnCurso() {
        return rutaDAO.findEnCurso();
    }

    public List<Ruta> listarPorVehiculo(String placa) {
        return rutaDAO.findByVehiculo(placa);
    }

    public List<Ruta> listarPorConductor(String conductorId) {
        return rutaDAO.findByConductor(conductorId);
    }

    public List<RutaPaquete> obtenerPaquetesDeRuta(Long idRuta) {
        return rutaPaqueteDAO.findByRutaId(idRuta);
    }

    public BigDecimal calcularPesoTotalRuta(Long idRuta) {
        return rutaPaqueteDAO.getPesoTotalRuta(idRuta);
    }
}
