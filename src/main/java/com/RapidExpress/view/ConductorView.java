package com.RapidExpress.view;

import com.RapidExpress.model.entity.Conductor;
import com.RapidExpress.model.enums.EstadoConductor;
import com.RapidExpress.model.enums.TipoLicencia;
import com.RapidExpress.util.DataValidator;

import java.util.List;

/**
 * Vista de consola para la gestión de conductores.
 * Muestra ejemplos representativos y valida datos según database/README.md.
 */
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
        return ConsoleUtil.readValidatedString(prompt, "1000000001", DataValidator::validarIdentificacionConductor);
    }

    public static Conductor readNewConductorData() {
        ConsoleUtil.printSubHeader("DATOS DEL NUEVO CONDUCTOR");
        ConsoleUtil.printInfo("Formato de datos aceptados según database/README.md:");
        ConsoleUtil.printInfo("- ID: Dígitos numéricos (ej: 1000000001)");
        ConsoleUtil.printInfo("- Nombre: Nombre y apellido válido (ej: Carlos Andres Rodriguez)");
        ConsoleUtil.printInfo("- Licencia: Categoría de transporte pesado C, D o E (ej: C)");
        ConsoleUtil.printInfo("- Contacto: Teléfono válido (ej: 3001112233)");

        String id = readIdentificacion("Número de identificación");
        String nombre = ConsoleUtil.readValidatedString("Nombre completo", "Carlos Andres Rodriguez",
                DataValidator::validarNombreCompleto);
        TipoLicencia lic = readTipoLicencia();
        String contacto = ConsoleUtil.readValidatedString("Número de contacto (teléfono)", "3001112233",
                val -> DataValidator.validarContacto(val, "Número de contacto"));

        return new Conductor(id, nombre, lic, contacto, EstadoConductor.ACTIVO, null);
    }

    public static Conductor readUpdatedConductorData(Conductor c) {
        ConsoleUtil.printSubHeader("ACTUALIZACION DE CONDUCTOR");
        ConsoleUtil.printInfo("Presione ENTER para mantener el valor actual o ingrese el nuevo valor.");

        String nombre = ConsoleUtil.readOptionalValidatedString("Nombre completo", c.getNombreCompleto(),
                "Carlos Andres Rodriguez", DataValidator::validarNombreCompleto);
        TipoLicencia lic = readOptionalTipoLicencia(c.getTipoLicencia());
        String contacto = ConsoleUtil.readOptionalValidatedString("Número de contacto", c.getNumContacto(),
                "3001112233", val -> DataValidator.validarContacto(val, "Número de contacto"));

        c.setNombreCompleto(nombre);
        c.setTipoLicencia(lic);
        c.setNumContacto(contacto);
        return c;
    }

    public static TipoLicencia readTipoLicencia() {
        TipoLicencia[] licenciasValidas = {TipoLicencia.C, TipoLicencia.D, TipoLicencia.E};
        System.out.println("Seleccione el tipo de licencia (según BD: C, D, E):");
        for (int i = 0; i < licenciasValidas.length; i++) {
            System.out.println("  " + (i + 1) + ". " + licenciasValidas[i]);
        }
        int idx = ConsoleUtil.readInt("Seleccione", "1", 1, licenciasValidas.length);
        return licenciasValidas[idx - 1];
    }

    public static TipoLicencia readOptionalTipoLicencia(TipoLicencia actual) {
        System.out.println("Tipo de licencia actual: " + actual);
        if (ConsoleUtil.readConfirmation("¿Desea cambiar el tipo de licencia?")) {
            return readTipoLicencia();
        }
        return actual;
    }

    public static EstadoConductor readNewEstado() {
        return ConsoleUtil.readEnum(EstadoConductor.class, "Seleccione el nuevo estado del conductor:");
    }

    public static String readVehiculoPlaca() {
        return ConsoleUtil.readValidatedString("Placa del vehículo a asignar", "ABC-123", DataValidator::validarPlaca);
    }

    public static void showConductorDetail(Conductor c) {
        ConsoleUtil.printSubHeader("DETALLE DEL CONDUCTOR");
        System.out.println("ID:             " + c.getNumIdentificacion());
        System.out.println("Nombre:         " + c.getNombreCompleto());
        System.out.println("Licencia:       " + c.getTipoLicencia());
        System.out.println("Contacto:       " + c.getNumContacto());
        System.out.println("Estado:         " + c.getEstado());
        System.out.println("Vehículo:       " + (c.getVehiculoAsignado() != null ? c.getVehiculoAsignado() : "Ninguno"));
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
        ConsoleUtil.printTable(new String[]{"ID", "Nombre", "Lic.", "Contacto", "Estado", "Vehículo"}, rows);
    }

    public static void showConductoresByEstado(List<Conductor> conductores, EstadoConductor estado) {
        ConsoleUtil.printInfo("Conductores con estado: " + estado);
        showConductorList(conductores);
    }

    public static void showConductoresDisponibles(List<Conductor> conductores) {
        ConsoleUtil.printInfo("Conductores disponibles (sin vehículo asignado):");
        showConductorList(conductores);
    }

    public static void showConductoresByTipoLicencia(List<Conductor> conductores, TipoLicencia tipo) {
        ConsoleUtil.printInfo("Conductores con licencia categoría " + tipo + ":");
        showConductorList(conductores);
    }

    public static void showAsignacionInfo(String conductorId, String placa) {
        ConsoleUtil.printSuccess("Vehículo " + placa + " asignado exitosamente al conductor " + conductorId);
    }

    public static void showLiberacionInfo(String conductorId) {
        ConsoleUtil.printSuccess("Vehículo liberado exitosamente del conductor " + conductorId);
    }

    public static boolean confirmDelete(String id) {
        return ConsoleUtil.readConfirmation("¿Confirma la eliminación del conductor " + id + "?");
    }
}