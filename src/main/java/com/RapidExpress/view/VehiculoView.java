package com.RapidExpress.view;

import com.RapidExpress.model.entity.Vehiculo;
import com.RapidExpress.model.enums.EstadoVehiculo;

import java.math.BigDecimal;
import java.util.List;

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
        return ConsoleUtil.readRequiredString(prompt).toUpperCase();
    }

    public static Vehiculo readNewVehiculoData() {
        ConsoleUtil.printSubHeader("Datos del nuevo vehiculo");
        String placa = readPlaca("Placa");
        String marca = ConsoleUtil.readRequiredString("Marca");
        String modelo = ConsoleUtil.readRequiredString("Modelo");
        int anio = ConsoleUtil.readInt("Anio de fabricacion", 1990, 2030);
        BigDecimal cap = ConsoleUtil.readBigDecimal("Capacidad maxima (kg)");
        return new Vehiculo(placa, marca, modelo, anio, cap, EstadoVehiculo.DISPONIBLE);
    }

    public static Vehiculo readUpdatedVehiculoData(Vehiculo v) {
        ConsoleUtil.printSubHeader("Deje vacio para mantener el valor actual");
        String marca = ConsoleUtil.readString("Marca [" + v.getMarca() + "]");
        String modelo = ConsoleUtil.readString("Modelo [" + v.getModelo() + "]");
        String anioStr = ConsoleUtil.readString("Anio [" + v.getAnioFabricacion() + "]");
        String capStr = ConsoleUtil.readString("Capacidad kg [" + v.getCapacidadMaxKg() + "]");
        if (!marca.isEmpty()) v.setMarca(marca);
        if (!modelo.isEmpty()) v.setModelo(modelo);
        if (!anioStr.isEmpty()) v.setAnioFabricacion(Integer.parseInt(anioStr));
        if (!capStr.isEmpty()) v.setCapacidadMaxKg(new BigDecimal(capStr));
        return v;
    }

    public static EstadoVehiculo readNewEstado() {
        return ConsoleUtil.readEnum(EstadoVehiculo.class, "Seleccione el nuevo estado:");
    }

    public static void showVehiculoDetail(Vehiculo v) {
        ConsoleUtil.printSubHeader("DETALLE DEL VEHICULO");
        System.out.println("Placa:         " + v.getPlaca());
        System.out.println("Marca:         " + v.getMarca());
        System.out.println("Modelo:        " + v.getModelo());
        System.out.println("Anio:          " + v.getAnioFabricacion());
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
        ConsoleUtil.printTable(new String[]{"Placa", "Marca", "Modelo", "Anio", "Capacidad", "Estado"}, rows);
    }

    public static void showVehiculosByEstado(List<Vehiculo> vehiculos, EstadoVehiculo estado) {
        ConsoleUtil.printInfo("Vehiculos con estado: " + estado);
        showVehiculoList(vehiculos);
    }

    public static void showStats(int total, int disponibles, int enRuta, int enMant) {
        ConsoleUtil.printSubHeader("ESTADISTICAS DE FLOTA");
        System.out.println("Total:             " + total);
        System.out.println("Disponibles:       " + disponibles);
        System.out.println("En ruta:           " + enRuta);
        System.out.println("En mantenimiento:  " + enMant);
    }

    public static boolean confirmDelete(String placa) {
        return ConsoleUtil.readConfirmation("Eliminar vehiculo " + placa + "?");
    }
}