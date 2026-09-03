package com.RapidExpress.view;

import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoVehiculo;
import com.RapidExpress.util.DataValidator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vista de consola para la gestión del parque automotor (vehículos).
 * Muestra ejemplos representativos y valida datos según database/README.md.
 */
public final class VehiculoView {

    private VehiculoView() {}

    public static void showMenu() {
        ConsoleUtil.printHeader("GESTION DE VEHICULOS");
        System.out.println("1. Registrar vehiculo");
        System.out.println("2. Listar todos");
        System.out.println("3. Buscar por placa");
        System.out.println("4. Actualizar vehiculo");
        System.out.println("5. Cambiar estado");
        System.out.println("6. Eliminar vehiculo");
        System.out.println("7. Filtrar por estado");
        System.out.println("8. Estadisticas de flota");
        System.out.println("0. Volver");
    }

    public static int readMenuOption() { return ConsoleUtil.readMenuOption(); }

    public static String readPlaca(String prompt) {
        return ConsoleUtil.readValidatedString(prompt, "ABC-123", DataValidator::validarPlaca);
    }

    public static Vehiculo readNewVehiculoData() {
        ConsoleUtil.printSubHeader("DATOS DEL NUEVO VEHICULO");
        ConsoleUtil.printInfo("Formato de datos aceptados: Placa (ej: ABC-123), Año [1990-2030], Capacidad > 0 kg");

        String placa = readPlaca("Placa del vehículo");
        String marca = ConsoleUtil.readRequiredString("Marca", "Chevrolet");
        String modelo = ConsoleUtil.readRequiredString("Modelo", "NPR 700");
        int anio = ConsoleUtil.readInt("Año de fabricación", "2020", 1990, 2030);
        BigDecimal cap = ConsoleUtil.readPositiveBigDecimal("Capacidad máxima de carga en kg", "5000.00");

        return new Vehiculo(placa, marca, modelo, anio, cap, EstadoVehiculo.DISPONIBLE);
    }

    public static Vehiculo readUpdatedVehiculoData(Vehiculo v) {
        ConsoleUtil.printSubHeader("ACTUALIZACION DE VEHICULO");
        ConsoleUtil.printInfo("Presione ENTER para mantener el valor actual o ingrese el nuevo valor.");

        String marca = ConsoleUtil.readOptionalString("Marca", v.getMarca(), "Chevrolet");
        String modelo = ConsoleUtil.readOptionalString("Modelo", v.getModelo(), "NPR 700");
        Integer anio = ConsoleUtil.readOptionalInt("Año de fabricación", v.getAnioFabricacion(), "2020", 1990, 2030);
        BigDecimal cap = ConsoleUtil.readOptionalPositiveBigDecimal("Capacidad máxima (kg)", v.getCapacidadMaxKg(), "5000.00");

        v.setMarca(marca);
        v.setModelo(modelo);
        v.setAnioFabricacion(anio);
        v.setCapacidadMaxKg(cap);
        return v;
    }

    public static EstadoVehiculo readNewEstado() {
        return ConsoleUtil.readEnum(EstadoVehiculo.class, "Seleccione el nuevo estado del vehículo:");
    }

    public static void showVehiculoDetail(Vehiculo v) {
        ConsoleUtil.printSubHeader("DETALLE DEL VEHICULO");
        System.out.println("Placa:         " + v.getPlaca());
        System.out.println("Marca:         " + v.getMarca());
        System.out.println("Modelo:        " + v.getModelo());
        System.out.println("Año:           " + v.getAnioFabricacion());
        System.out.println("Capacidad:     " + v.getCapacidadMaxKg() + " kg");
        System.out.println("Estado:        " + v.getEstado());
    }

    public static void showVehiculoList(List<Vehiculo> vehiculos) {
        if (vehiculos.isEmpty()) { ConsoleUtil.printInfo("No hay vehiculos."); return; }
        String[][] rows = new String[vehiculos.size()][6];
        for (int i = 0; i < vehiculos.size(); i++) {
            Vehiculo v = vehiculos.get(i);
            rows[i] = new String[]{v.getPlaca(), v.getMarca(), v.getModelo(),
                    String.valueOf(v.getAnioFabricacion()), v.getCapacidadMaxKg() + " kg", v.getEstado().toString()};
        }
        ConsoleUtil.printTable(new String[]{"Placa", "Marca", "Modelo", "Año", "Capacidad", "Estado"}, rows);
    }

    public static void showVehiculosByEstado(List<Vehiculo> vehiculos, EstadoVehiculo estado) {
        ConsoleUtil.printInfo("Vehículos con estado: " + estado);
        showVehiculoList(vehiculos);
    }

    public static void showStats(int total, int disponibles, int enRuta, int enMant) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE FLOTA");
        System.out.println("Total vehículos:    " + total);
        System.out.println("Disponibles:        " + disponibles);
        System.out.println("En ruta:            " + enRuta);
        System.out.println("En mantenimiento:   " + enMant);
    }

    public static boolean confirmDelete(String placa) {
        return ConsoleUtil.readConfirmation("¿Confirma la eliminación del vehículo " + placa + "?");
    }
}