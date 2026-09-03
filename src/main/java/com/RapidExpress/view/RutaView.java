package com.RapidExpress.view;

import com.RapidExpress.model.entity.Ruta;
import com.RapidExpress.model.enums.EstadoRuta;
import com.RapidExpress.util.DataValidator;

import java.time.LocalDate;
import java.util.List;

/**
 * Vista de consola para la gestión y seguimiento de rutas de transporte.
 * Muestra ejemplos representativos y valida datos según database/README.md.
 */
public final class RutaView {

    private RutaView() {}

    public static void showMenu() {
        ConsoleUtil.printHeader("GESTION DE RUTAS");
        System.out.println("1. Crear ruta");
        System.out.println("2. Listar todas");
        System.out.println("3. Buscar por ID");
        System.out.println("4. Actualizar ruta");
        System.out.println("5. Cambiar estado");
        System.out.println("6. Iniciar ruta");
        System.out.println("7. Completar ruta");
        System.out.println("8. Cancelar ruta");
        System.out.println("9. Filtrar por estado");
        System.out.println("10. Filtrar por fecha");
        System.out.println("11. Filtrar por vehiculo");
        System.out.println("12. Filtrar por conductor");
        System.out.println("13. Estadisticas");
        System.out.println("0. Volver");
    }

    public static int readMenuOption() { return ConsoleUtil.readMenuOption(); }

    public static Long readIdRuta(String prompt) {
        return (long) ConsoleUtil.readInt(prompt, "1", 1, Integer.MAX_VALUE);
    }

    public static Ruta readNewRutaData() {
        ConsoleUtil.printSubHeader("DATOS DE LA NUEVA RUTA");
        ConsoleUtil.printInfo("Formato de datos aceptados: Fecha (dd/MM/yyyy), Placa (ej: ABC-125), Conductor (ej: 1000000003)");

        LocalDate fecha = ConsoleUtil.readDate("Fecha de ejecución de la ruta", "15/01/2024");
        String placa = ConsoleUtil.readValidatedString("Placa del vehículo", "ABC-125", DataValidator::validarPlaca);
        String conductor = ConsoleUtil.readValidatedString("ID del conductor asignado", "1000000003", DataValidator::validarIdentificacionConductor);

        return new Ruta(null, fecha, placa, conductor, EstadoRuta.PLANIFICADA);
    }

    public static Ruta readUpdatedRutaData(Ruta r) {
        ConsoleUtil.printSubHeader("ACTUALIZACION DE RUTA");
        ConsoleUtil.printInfo("Presione ENTER para mantener el valor actual o ingrese el nuevo valor.");

        LocalDate fecha = ConsoleUtil.readOptionalDate("Fecha de ruta", r.getFecha(), "15/01/2024");

        String placaActual = r.getVehiculoPlaca();
        String placa = ConsoleUtil.readOptionalString("Placa del vehículo", placaActual, "ABC-125");
        if (!placa.equalsIgnoreCase(placaActual)) {
            try {
                placa = DataValidator.validarPlaca(placa);
            } catch (Exception e) {
                ConsoleUtil.printError(e.getMessage());
                placa = placaActual;
            }
        }

        String conductorActual = r.getConductorId();
        String conductor = ConsoleUtil.readOptionalString("ID del conductor", conductorActual, "1000000003");
        if (!conductor.equals(conductorActual)) {
            try {
                conductor = DataValidator.validarIdentificacionConductor(conductor);
            } catch (Exception e) {
                ConsoleUtil.printError(e.getMessage());
                conductor = conductorActual;
            }
        }

        r.setFecha(fecha);
        r.setVehiculoPlaca(placa);
        r.setConductorId(conductor);
        return r;
    }

    public static EstadoRuta readNewEstado() {
        return ConsoleUtil.readEnum(EstadoRuta.class, "Seleccione el nuevo estado de la ruta:");
    }

    public static void showRutaDetail(Ruta r) {
        ConsoleUtil.printSubHeader("DETALLE DE LA RUTA");
        System.out.println("ID:        " + r.getIdRuta());
        System.out.println("Fecha:     " + r.getFecha());
        System.out.println("Vehículo:  " + r.getVehiculoPlaca());
        System.out.println("Conductor: " + r.getConductorId());
        System.out.println("Estado:    " + r.getEstado());
    }

    public static void showRutaList(List<Ruta> rutas) {
        if (rutas.isEmpty()) { ConsoleUtil.printInfo("No hay rutas registradas."); return; }
        String[][] rows = new String[rutas.size()][5];
        for (int i = 0; i < rutas.size(); i++) {
            Ruta r = rutas.get(i);
            rows[i] = new String[]{String.valueOf(r.getIdRuta()), r.getFecha().toString(),
                    r.getVehiculoPlaca(), r.getConductorId(), r.getEstado().toString()};
        }
        ConsoleUtil.printTable(new String[]{"ID", "Fecha", "Vehículo", "Conductor", "Estado"}, rows);
    }

    public static void showRutasByEstado(List<Ruta> rutas, EstadoRuta estado) {
        ConsoleUtil.printInfo("Rutas con estado: " + estado);
        showRutaList(rutas);
    }

    public static void showRutasByFecha(List<Ruta> rutas, LocalDate fecha) {
        ConsoleUtil.printInfo("Rutas programadas para el: " + fecha);
        showRutaList(rutas);
    }

    public static void showRutasByVehiculo(List<Ruta> rutas, String placa) {
        ConsoleUtil.printInfo("Rutas del vehículo: " + placa);
        showRutaList(rutas);
    }

    public static void showRutasByConductor(List<Ruta> rutas, String conductor) {
        ConsoleUtil.printInfo("Rutas asignadas al conductor: " + conductor);
        showRutaList(rutas);
    }

    public static void showStats(int total, int planificadas, int enCurso, int completadas, int canceladas) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE RUTAS");
        System.out.println("Total rutas:     " + total);
        System.out.println("Planificadas:    " + planificadas);
        System.out.println("En curso:        " + enCurso);
        System.out.println("Completadas:     " + completadas);
        System.out.println("Canceladas:      " + canceladas);
    }

    public static boolean confirmDelete(Long id) {
        return ConsoleUtil.readConfirmation("¿Confirma la cancelación/eliminación de la ruta " + id + "?");
    }
}