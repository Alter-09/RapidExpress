package com.RapidExpress.view;

import com.RapidExpress.model.entity.Ruta;
import com.RapidExpress.model.enums.EstadoRuta;

import java.time.LocalDate;
import java.util.List;

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
        return (long) ConsoleUtil.readInt(prompt);
    }

    public static Ruta readNewRutaData() {
        ConsoleUtil.printSubHeader("Datos de la nueva ruta");
        LocalDate fecha = ConsoleUtil.readDate("Fecha");
        String placa = ConsoleUtil.readRequiredString("Placa del vehiculo").toUpperCase();
        String conductor = ConsoleUtil.readRequiredString("ID del conductor");
        return new Ruta(null, fecha, placa, conductor, EstadoRuta.PLANIFICADA);
    }

    public static Ruta readUpdatedRutaData(Ruta r) {
        ConsoleUtil.printSubHeader("Deje vacio para mantener el valor actual");
        String fechaStr = ConsoleUtil.readString("Fecha [" + r.getFecha() + "]");
        String placa = ConsoleUtil.readString("Placa vehiculo [" + r.getVehiculoPlaca() + "]");
        String conductor = ConsoleUtil.readString("Conductor [" + r.getConductorId() + "]");
        if (!fechaStr.isEmpty()) r.setFecha(LocalDate.parse(fechaStr, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (!placa.isEmpty()) r.setVehiculoPlaca(placa);
        if (!conductor.isEmpty()) r.setConductorId(conductor);
        return r;
    }

    public static EstadoRuta readNewEstado() {
        return ConsoleUtil.readEnum(EstadoRuta.class, "Seleccione el nuevo estado:");
    }

    public static void showRutaDetail(Ruta r) {
        ConsoleUtil.printSubHeader("DETALLE DE LA RUTA");
        System.out.println("ID:        " + r.getIdRuta());
        System.out.println("Fecha:     " + r.getFecha());
        System.out.println("Vehiculo:  " + r.getVehiculoPlaca());
        System.out.println("Conductor: " + r.getConductorId());
        System.out.println("Estado:    " + r.getEstado());
    }

    public static void showRutaList(List<Ruta> rutas) {
        if (rutas.isEmpty()) { ConsoleUtil.printInfo("No hay rutas."); return; }
        String[][] rows = new String[rutas.size()][5];
        for (int i = 0; i < rutas.size(); i++) {
            Ruta r = rutas.get(i);
            rows[i] = new String[]{String.valueOf(r.getIdRuta()), r.getFecha().toString(),
                    r.getVehiculoPlaca(), r.getConductorId(), r.getEstado().toString()};
        }
        ConsoleUtil.printTable(new String[]{"ID", "Fecha", "Vehiculo", "Conductor", "Estado"}, rows);
    }

    public static void showRutasByEstado(List<Ruta> rutas, EstadoRuta estado) {
        ConsoleUtil.printInfo("Rutas con estado: " + estado);
        showRutaList(rutas);
    }

    public static void showRutasByFecha(List<Ruta> rutas, LocalDate fecha) {
        ConsoleUtil.printInfo("Rutas del " + fecha + ":");
        showRutaList(rutas);
    }

    public static void showRutasByVehiculo(List<Ruta> rutas, String placa) {
        ConsoleUtil.printInfo("Rutas del vehiculo " + placa + ":");
        showRutaList(rutas);
    }

    public static void showRutasByConductor(List<Ruta> rutas, String conductor) {
        ConsoleUtil.printInfo("Rutas del conductor " + conductor + ":");
        showRutaList(rutas);
    }

    public static void showStats(int total, int planificadas, int enCurso, int completadas, int canceladas) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE RUTAS");
        System.out.println("Total:        " + total);
        System.out.println("Planificadas: " + planificadas);
        System.out.println("En curso:     " + enCurso);
        System.out.println("Completadas:  " + completadas);
        System.out.println("Canceladas:   " + canceladas);
    }

    public static boolean confirmDelete(Long id) {
        return ConsoleUtil.readConfirmation("Eliminar ruta " + id + "?");
    }
}