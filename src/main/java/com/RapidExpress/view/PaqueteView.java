package com.RapidExpress.view;

import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.enums.EstadoPaquete;

import java.math.BigDecimal;
import java.util.List;

public final class PaqueteView {

    private PaqueteView() {}

    public static void showMenu() {
        ConsoleUtil.printHeader("GESTION DE PAQUETES");
        System.out.println("1. Registrar paquete");
        System.out.println("2. Listar todos");
        System.out.println("3. Buscar por ID de seguimiento");
        System.out.println("4. Actualizar paquete");
        System.out.println("5. Cambiar estado");
        System.out.println("6. Eliminar paquete");
        System.out.println("7. Filtrar por estado");
        System.out.println("8. Buscar por remitente");
        System.out.println("9. Buscar por destinatario");
        System.out.println("10. Ver en bodega");
        System.out.println("0. Volver");
    }

    public static int readMenuOption() { return ConsoleUtil.readMenuOption(); }

    public static String readIdSeguimiento(String prompt) {
        return ConsoleUtil.readRequiredString(prompt);
    }

    public static Paquete readNewPaqueteData() {
        ConsoleUtil.printSubHeader("Datos del nuevo paquete");
        String desc = ConsoleUtil.readRequiredString("Descripcion");
        BigDecimal peso = ConsoleUtil.readBigDecimal("Peso (kg)");
        String dim = ConsoleUtil.readRequiredString("Dimensiones (ej: 30x20x15 cm)");
        String origen = ConsoleUtil.readRequiredString("Direccion de origen");
        String destino = ConsoleUtil.readRequiredString("Direccion de destino");
        String remNom = ConsoleUtil.readRequiredString("Nombre del remitente");
        String remCon = ConsoleUtil.readRequiredString("Contacto del remitente");
        String remDir = ConsoleUtil.readRequiredString("Direccion del remitente");
        String destNom = ConsoleUtil.readRequiredString("Nombre del destinatario");
        String destCon = ConsoleUtil.readRequiredString("Contacto del destinatario");
        String destDir = ConsoleUtil.readRequiredString("Direccion del destinatario");
        return new Paquete(null, desc, peso, dim, origen, destino,
                remNom, remCon, remDir, destNom, destCon, destDir, EstadoPaquete.EN_BODEGA);
    }

    public static Paquete readUpdatedPaqueteData(Paquete p) {
        ConsoleUtil.printSubHeader("Deje vacio para mantener el valor actual");
        String desc = ConsoleUtil.readString("Descripcion [" + p.getDescripcion() + "]");
        String pesoStr = ConsoleUtil.readString("Peso kg [" + p.getPesoKg() + "]");
        String dim = ConsoleUtil.readString("Dimensiones [" + p.getDimensiones() + "]");
        String origen = ConsoleUtil.readString("Origen [" + p.getDirOrigen() + "]");
        String destino = ConsoleUtil.readString("Destino [" + p.getDirDestino() + "]");
        String remNom = ConsoleUtil.readString("Remitente [" + p.getRemitenteNombre() + "]");
        String remCon = ConsoleUtil.readString("Contacto rem. [" + p.getRemitenteContacto() + "]");
        String remDir = ConsoleUtil.readString("Dir rem. [" + p.getRemitenteDireccion() + "]");
        String destNom = ConsoleUtil.readString("Destinatario [" + p.getDestinatarioNombre() + "]");
        String destCon = ConsoleUtil.readString("Contacto dest. [" + p.getDestinatarioContacto() + "]");
        String destDir = ConsoleUtil.readString("Dir dest. [" + p.getDestinatarioDireccion() + "]");
        if (!desc.isEmpty()) p.setDescripcion(desc);
        if (!pesoStr.isEmpty()) p.setPesoKg(new BigDecimal(pesoStr));
        if (!dim.isEmpty()) p.setDimensiones(dim);
        if (!origen.isEmpty()) p.setDirOrigen(origen);
        if (!destino.isEmpty()) p.setDirDestino(destino);
        if (!remNom.isEmpty()) p.setRemitenteNombre(remNom);
        if (!remCon.isEmpty()) p.setRemitenteContacto(remCon);
        if (!remDir.isEmpty()) p.setRemitenteDireccion(remDir);
        if (!destNom.isEmpty()) p.setDestinatarioNombre(destNom);
        if (!destCon.isEmpty()) p.setDestinatarioContacto(destCon);
        if (!destDir.isEmpty()) p.setDestinatarioDireccion(destDir);
        return p;
    }

    public static EstadoPaquete readNewEstado() {
        return ConsoleUtil.readEnum(EstadoPaquete.class, "Seleccione el nuevo estado:");
    }

    public static String readRemitenteNombre() {
        return ConsoleUtil.readRequiredString("Nombre del remitente");
    }

    public static String readDestinatarioNombre() {
        return ConsoleUtil.readRequiredString("Nombre del destinatario");
    }

    public static void showPaqueteDetail(Paquete p) {
        ConsoleUtil.printSubHeader("DETALLE DEL PAQUETE");
        System.out.println("ID Seguimiento:  " + p.getIdSeguimiento());
        System.out.println("Descripcion:     " + p.getDescripcion());
        System.out.println("Peso:            " + p.getPesoKg() + " kg");
        System.out.println("Dimensiones:     " + p.getDimensiones());
        System.out.println("Origen:          " + p.getDirOrigen());
        System.out.println("Destino:         " + p.getDirDestino());
        System.out.println("Remitente:       " + p.getRemitenteNombre() + " (" + p.getRemitenteContacto() + ")");
        System.out.println("Destinatario:    " + p.getDestinatarioNombre() + " (" + p.getDestinatarioContacto() + ")");
        System.out.println("Estado:          " + p.getEstado());
    }

    public static void showPaqueteList(List<Paquete> paquetes) {
        if (paquetes.isEmpty()) { ConsoleUtil.printInfo("No hay paquetes."); return; }
        String[][] rows = new String[paquetes.size()][6];
        for (int i = 0; i < paquetes.size(); i++) {
            Paquete p = paquetes.get(i);
            rows[i] = new String[]{p.getIdSeguimiento().substring(0, Math.min(8, p.getIdSeguimiento().length())),
                    p.getDescripcion(), p.getPesoKg() + " kg", p.getDirOrigen(), p.getDirDestino(), p.getEstado().toString()};
        }
        ConsoleUtil.printTable(new String[]{"ID", "Descripcion", "Peso", "Origen", "Destino", "Estado"}, rows);
    }

    public static void showPaquetesByEstado(List<Paquete> paquetes, EstadoPaquete estado) {
        ConsoleUtil.printInfo("Paquetes con estado: " + estado);
        showPaqueteList(paquetes);
    }

    public static void showPaquetesByRemitente(List<Paquete> paquetes, String remitente) {
        ConsoleUtil.printInfo("Paquetes de remitente: " + remitente);
        showPaqueteList(paquetes);
    }

    public static void showPaquetesByDestinatario(List<Paquete> paquetes, String destinatario) {
        ConsoleUtil.printInfo("Paquetes para destinatario: " + destinatario);
        showPaqueteList(paquetes);
    }

    public static boolean confirmDelete(String id) {
        return ConsoleUtil.readConfirmation("Eliminar paquete " + id + "?");
    }
}