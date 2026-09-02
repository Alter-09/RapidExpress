package com.RapidExpress.view;

import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.TipoLicencia;

import java.util.List;

public final class ConductorView {

    private ConductorView() {}

    public static void showMenu() {
        ConsoleUtil.printHeader("GESTION DE CONDUCTORES");
        System.out.println("1. Registrar conductor");
        System.out.println("2. Listar todos");
        System.out.println("3. Buscar por identificacion");
        System.out.println("4. Actualizar conductor");
        System.out.println("5. Cambiar estado");
        System.out.println("6. Asignar vehiculo");
        System.out.println("7. Liberar vehiculo");
        System.out.println("8. Eliminar conductor");
        System.out.println("9. Filtrar por estado");
        System.out.println("10. Ver disponibles");
        System.out.println("11. Filtrar por tipo de licencia");
        System.out.println("0. Volver");
    }

    public static int readMenuOption() { return ConsoleUtil.readMenuOption(); }

    public static String readIdentificacion(String prompt) {
        return ConsoleUtil.readRequiredString(prompt);
    }

    public static Conductor readNewConductorData() {
        ConsoleUtil.printSubHeader("Datos del nuevo conductor");
        String id = readIdentificacion("Numero de identificacion");
        String nombre = ConsoleUtil.readRequiredString("Nombre completo");
        TipoLicencia lic = ConsoleUtil.readEnum(TipoLicencia.class, "Tipo de licencia:");
        String contacto = ConsoleUtil.readRequiredString("Numero de contacto");
        return new Conductor(id, nombre, lic, contacto, EstadoConductor.ACTIVO, null);
    }

    public static Conductor readUpdatedConductorData(Conductor c) {
        ConsoleUtil.printSubHeader("Deje vacio para mantener el valor actual");
        String nombre = ConsoleUtil.readString("Nombre [" + c.getNombreCompleto() + "]");
        String licStr = ConsoleUtil.readString("Licencia [" + c.getTipoLicencia().getCodigo() + "]");
        String contacto = ConsoleUtil.readString("Contacto [" + c.getNumContacto() + "]");
        String vehiculo = ConsoleUtil.readString("Vehiculo [" + (c.getVehiculoAsignado() != null ? c.getVehiculoAsignado() : "ninguno") + "]");
        if (!nombre.isEmpty()) c.setNombreCompleto(nombre);
        if (!licStr.isEmpty()) c.setTipoLicencia(TipoLicencia.fromString(licStr));
        if (!contacto.isEmpty()) c.setNumContacto(contacto);
        if (!vehiculo.isEmpty()) c.setVehiculoAsignado(vehiculo);
        return c;
    }

    public static EstadoConductor readNewEstado() {
        return ConsoleUtil.readEnum(EstadoConductor.class, "Seleccione el nuevo estado:");
    }

    public static String readVehiculoPlaca() {
        return ConsoleUtil.readRequiredString("Placa del vehiculo a asignar").toUpperCase();
    }

    public static void showConductorDetail(Conductor c) {
        ConsoleUtil.printSubHeader("DETALLE DEL CONDUCTOR");
        System.out.println("ID:             " + c.getNumIdentificacion());
        System.out.println("Nombre:         " + c.getNombreCompleto());
        System.out.println("Licencia:       " + c.getTipoLicencia());
        System.out.println("Contacto:       " + c.getNumContacto());
        System.out.println("Estado:         " + c.getEstado());
        System.out.println("Vehiculo:       " + (c.getVehiculoAsignado() != null ? c.getVehiculoAsignado() : "Ninguno"));
    }

    public static void showConductorList(List<Conductor> conductores) {
        if (conductores.isEmpty()) { ConsoleUtil.printInfo("No hay conductores."); return; }
        String[][] rows = new String[conductores.size()][6];
        for (int i = 0; i < conductores.size(); i++) {
            Conductor c = conductores.get(i);
            rows[i] = new String[]{c.getNumIdentificacion(), c.getNombreCompleto(),
                    c.getTipoLicencia().getCodigo(), c.getNumContacto(), c.getEstado().toString(),
                    c.getVehiculoAsignado() != null ? c.getVehiculoAsignado() : "-"};
        }
        ConsoleUtil.printTable(new String[]{"ID", "Nombre", "Lic.", "Contacto", "Estado", "Vehiculo"}, rows);
    }

    public static void showConductoresByEstado(List<Conductor> conductores, EstadoConductor estado) {
        ConsoleUtil.printInfo("Conductores con estado: " + estado);
        showConductorList(conductores);
    }

    public static void showConductoresDisponibles(List<Conductor> conductores) {
        ConsoleUtil.printInfo("Conductores disponibles (sin vehiculo asignado):");
        showConductorList(conductores);
    }

    public static void showConductoresByTipoLicencia(List<Conductor> conductores, TipoLicencia tipo) {
        ConsoleUtil.printInfo("Conductores con licencia " + tipo + ":");
        showConductorList(conductores);
    }

    public static void showAsignacionInfo(String conductorId, String placa) {
        ConsoleUtil.printSuccess("Vehiculo " + placa + " asignado al conductor " + conductorId);
    }

    public static void showLiberacionInfo(String conductorId) {
        ConsoleUtil.printSuccess("Vehiculo liberado del conductor " + conductorId);
    }

    public static boolean confirmDelete(String id) {
        return ConsoleUtil.readConfirmation("Eliminar conductor " + id + "?");
    }
}