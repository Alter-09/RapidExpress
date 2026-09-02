package com.RapidExpress.view;

import com.RapidExpress.model.entity.Paquete;
import com.RapidExpress.model.enums.EstadoPaquete;
import com.RapidExpress.util.DataValidator;

import java.math.BigDecimal;
import java.util.List;

/**
 * Vista de consola para la gestión de paquetes y envíos.
 * Muestra ejemplos representativos y valida datos según database/README.md.
 */
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
        return ConsoleUtil.readValidatedString(prompt, "PKG-20240115-001", DataValidator::validarIdSeguimiento);
    }

    public static Paquete readNewPaqueteData() {
        ConsoleUtil.printSubHeader("DATOS DEL NUEVO PAQUETE");
        ConsoleUtil.printInfo("Formato de datos aceptados: Peso > 0 kg, Dimensiones: Ancho x Alto x Profundidad (ej: 30x20x5)");

        String desc = ConsoleUtil.readRequiredString("Descripción del contenido", "Documentos legales");
        BigDecimal peso = ConsoleUtil.readPositiveBigDecimal("Peso en kg", "2.50");
        String dim = ConsoleUtil.readValidatedString("Dimensiones [AxAxP en cm]", "30x20x5", DataValidator::validarDimensiones);
        String origen = ConsoleUtil.readRequiredString("Dirección de origen", "Calle 26 # 68-85, Bogota");
        String destino = ConsoleUtil.readRequiredString("Dirección de destino", "Carrera 15 # 45-60, Medellin");

        String remNom = ConsoleUtil.readRequiredString("Nombre del remitente", "LegalTech SAS");
        String remCon = ConsoleUtil.readValidatedString("Contacto del remitente (teléfono)", "3001112233",
                val -> DataValidator.validarContacto(val, "Contacto del remitente"));
        String remDir = ConsoleUtil.readRequiredString("Dirección del remitente", "Calle 10 # 20-30");

        String destNom = ConsoleUtil.readRequiredString("Nombre del destinatario", "Abogados Asociados");
        String destCon = ConsoleUtil.readValidatedString("Contacto del destinatario (teléfono)", "3001112255",
                val -> DataValidator.validarContacto(val, "Contacto del destinatario"));
        String destDir = ConsoleUtil.readRequiredString("Dirección del destinatario", "Carrera 43 # 50-10");

        return new Paquete(null, desc, peso, dim, origen, destino,
                remNom, remCon, remDir, destNom, destCon, destDir, EstadoPaquete.EN_BODEGA);
    }

    public static Paquete readUpdatedPaqueteData(Paquete p) {
        ConsoleUtil.printSubHeader("ACTUALIZACION DE PAQUETE");
        ConsoleUtil.printInfo("Presione ENTER para mantener el valor actual o ingrese el nuevo valor.");

        String desc = ConsoleUtil.readOptionalString("Descripción", p.getDescripcion(), "Documentos legales");
        BigDecimal peso = ConsoleUtil.readOptionalPositiveBigDecimal("Peso kg", p.getPesoKg(), "2.50");

        String dimActual = p.getDimensiones();
        String dim = ConsoleUtil.readOptionalString("Dimensiones [AxAxP]", dimActual, "30x20x5");
        if (!dim.equals(dimActual)) {
            try {
                dim = DataValidator.validarDimensiones(dim);
            } catch (Exception e) {
                ConsoleUtil.printError(e.getMessage());
                dim = dimActual;
            }
        }

        String origen = ConsoleUtil.readOptionalString("Dirección origen", p.getDirOrigen(), "Calle 26 # 68-85, Bogota");
        String destino = ConsoleUtil.readOptionalString("Dirección destino", p.getDirDestino(), "Carrera 15 # 45-60, Medellin");
        String remNom = ConsoleUtil.readOptionalString("Nombre remitente", p.getRemitenteNombre(), "LegalTech SAS");

        String remConActual = p.getRemitenteContacto();
        String remCon = ConsoleUtil.readOptionalString("Contacto remitente", remConActual, "3001112233");
        if (!remCon.equals(remConActual)) {
            try {
                remCon = DataValidator.validarContacto(remCon, "Contacto remitente");
            } catch (Exception e) {
                ConsoleUtil.printError(e.getMessage());
                remCon = remConActual;
            }
        }

        String remDir = ConsoleUtil.readOptionalString("Dirección remitente", p.getRemitenteDireccion(), "Calle 10 # 20-30");
        String destNom = ConsoleUtil.readOptionalString("Nombre destinatario", p.getDestinatarioNombre(), "Abogados Asociados");

        String destConActual = p.getDestinatarioContacto();
        String destCon = ConsoleUtil.readOptionalString("Contacto destinatario", destConActual, "3001112255");
        if (!destCon.equals(destConActual)) {
            try {
                destCon = DataValidator.validarContacto(destCon, "Contacto destinatario");
            } catch (Exception e) {
                ConsoleUtil.printError(e.getMessage());
                destCon = destConActual;
            }
        }

        String destDir = ConsoleUtil.readOptionalString("Dirección destinatario", p.getDestinatarioDireccion(), "Carrera 43 # 50-10");

        p.setDescripcion(desc);
        p.setPesoKg(peso);
        p.setDimensiones(dim);
        p.setDirOrigen(origen);
        p.setDirDestino(destino);
        p.setRemitenteNombre(remNom);
        p.setRemitenteContacto(remCon);
        p.setRemitenteDireccion(remDir);
        p.setDestinatarioNombre(destNom);
        p.setDestinatarioContacto(destCon);
        p.setDestinatarioDireccion(destDir);
        return p;
    }

    public static EstadoPaquete readNewEstado() {
        return ConsoleUtil.readEnum(EstadoPaquete.class, "Seleccione el nuevo estado del paquete:");
    }

    public static String readRemitenteNombre() {
        return ConsoleUtil.readRequiredString("Nombre del remitente", "LegalTech SAS");
    }

    public static String readDestinatarioNombre() {
        return ConsoleUtil.readRequiredString("Nombre del destinatario", "Abogados Asociados");
    }

    public static void showPaqueteDetail(Paquete p) {
        ConsoleUtil.printSubHeader("DETALLE DEL PAQUETE");
        System.out.println("ID Seguimiento:  " + p.getIdSeguimiento());
        System.out.println("Descripción:     " + p.getDescripcion());
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
            rows[i] = new String[]{p.getIdSeguimiento(),
                    p.getDescripcion(), p.getPesoKg() + " kg", p.getDirOrigen(), p.getDirDestino(), p.getEstado().toString()};
        }
        ConsoleUtil.printTable(new String[]{"ID Seguimiento", "Descripcion", "Peso", "Origen", "Destino", "Estado"}, rows);
    }

    public static void showPaquetesByEstado(List<Paquete> paquetes, EstadoPaquete estado) {
        ConsoleUtil.printInfo("Paquetes con estado: " + estado);
        showPaqueteList(paquetes);
    }

    public static void showPaquetesByRemitente(List<Paquete> paquetes, String remitente) {
        ConsoleUtil.printInfo("Paquetes del remitente: " + remitente);
        showPaqueteList(paquetes);
    }

    public static void showPaquetesByDestinatario(List<Paquete> paquetes, String destinatario) {
        ConsoleUtil.printInfo("Paquetes para el destinatario: " + destinatario);
        showPaqueteList(paquetes);
    }

    public static boolean confirmDelete(String id) {
        return ConsoleUtil.readConfirmation("¿Confirma la eliminación del paquete " + id + "?");
    }
}