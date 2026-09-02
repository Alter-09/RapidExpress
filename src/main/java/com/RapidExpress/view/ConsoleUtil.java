package com.RapidExpress.view;

import com.RapidExpress.exception.DatoRequeridoException;
import com.RapidExpress.exception.FormatoInvalidoException;
import com.RapidExpress.exception.RangoInvalidoException;
import com.RapidExpress.exception.ValidacionException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Shared console I/O utility with robust exception handling and input validation.
 * Displays representative examples guided by database/README.md.
 */
public final class ConsoleUtil {

    private static final Scanner SC = new Scanner(System.in);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FunctionalInterface
    public interface StringValidator {
        String validate(String input) throws ValidacionException;
    }

    private ConsoleUtil() {}

    // --- Output ---
    public static void clearScreen() { System.out.println("\n".repeat(2)); }
    public static void pressEnterToContinue() { System.out.print("\nPresione ENTER para continuar..."); SC.nextLine(); }
    public static void printSuccess(String msg) { System.out.println("\n[OK] " + msg); }
    public static void printError(String msg)   { System.out.println("\n[ERROR] " + msg); }
    public static void printInfo(String msg)    { System.out.println("\n[INFO] " + msg); }
    public static void printSubHeader(String t) { System.out.println("\n--- " + t + " ---"); }
    public static void printHeader(String t)    { System.out.println("\n========== " + t + " =========="); }
    public static void printLine()             { System.out.println("-".repeat(50)); }
    public static void printRaw(String s)      { System.out.println(s); }

    // --- Input (General) ---
    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return SC.nextLine().trim();
    }

    public static String readString(String prompt, String example) {
        if (example != null && !example.isEmpty()) {
            System.out.print(prompt + " (ej: " + example + "): ");
        } else {
            System.out.print(prompt + ": ");
        }
        return SC.nextLine().trim();
    }

    public static String readRequiredString(String prompt) {
        return readRequiredString(prompt, null);
    }

    public static String readRequiredString(String prompt, String example) {
        while (true) {
            String s = readString(prompt, example);
            try {
                if (s.isEmpty()) {
                    throw new DatoRequeridoException(prompt, example != null ? example : "valor no vacío");
                }
                return s;
            } catch (DatoRequeridoException e) {
                printError(e.getMessage());
            }
        }
    }

    /**
     * Lee una cadena obligatoria validándola con una función que puede lanzar ValidacionException.
     */
    public static String readValidatedString(String prompt, String example, StringValidator validator) {
        while (true) {
            String raw = readString(prompt, example);
            try {
                return validator.validate(raw);
            } catch (ValidacionException e) {
                printError(e.getMessage());
            }
        }
    }

    public static int readInt(String prompt) {
        return readInt(prompt, null);
    }

    public static int readInt(String prompt, String example) {
        while (true) {
            System.out.print(example != null && !example.isEmpty()
                    ? prompt + " (ej: " + example + "): "
                    : prompt + ": ");
            try {
                return Integer.parseInt(SC.nextLine().trim());
            } catch (NumberFormatException e) {
                printError("Debe ingresar un número entero válido." + (example != null ? " Ejemplo aceptado: " + example : ""));
            }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        return readInt(prompt, String.valueOf(min), min, max);
    }

    public static int readInt(String prompt, String example, int min, int max) {
        while (true) {
            int v = readInt(prompt + " [" + min + " - " + max + "]", example);
            try {
                if (v < min || v > max) {
                    throw new RangoInvalidoException(prompt, v, min, max, example);
                }
                return v;
            } catch (RangoInvalidoException e) {
                printError(e.getMessage());
            }
        }
    }

    public static BigDecimal readBigDecimal(String prompt) {
        return readBigDecimal(prompt, null);
    }

    public static BigDecimal readBigDecimal(String prompt, String example) {
        while (true) {
            System.out.print(example != null && !example.isEmpty()
                    ? prompt + " (ej: " + example + "): "
                    : prompt + ": ");
            try {
                return new BigDecimal(SC.nextLine().trim());
            } catch (NumberFormatException e) {
                printError("Debe ingresar un número decimal válido." + (example != null ? " Ejemplo aceptado: " + example : ""));
            }
        }
    }

    public static BigDecimal readPositiveBigDecimal(String prompt, String example) {
        while (true) {
            BigDecimal val = readBigDecimal(prompt, example);
            try {
                if (val.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RangoInvalidoException(prompt, val, "> 0", "+inf", example);
                }
                return val;
            } catch (RangoInvalidoException e) {
                printError(e.getMessage());
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        return readDate(prompt, "15/01/2024");
    }

    public static LocalDate readDate(String prompt, String example) {
        while (true) {
            System.out.print(prompt + " (dd/MM/yyyy)" + (example != null ? " (ej: " + example + ")" : "") + ": ");
            String input = SC.nextLine().trim();
            try {
                if (input.isEmpty()) {
                    throw new DatoRequeridoException(prompt, example != null ? example : "15/01/2024");
                }
                return LocalDate.parse(input, FMT);
            } catch (DatoRequeridoException e) {
                printError(e.getMessage());
            } catch (DateTimeParseException e) {
                printError("Formato de fecha inválido. Debe usar formato dd/MM/yyyy (ej: " + (example != null ? example : "15/01/2024") + ").");
            }
        }
    }

    // --- Input Seguro para Actualizaciones (Permite dejar vacío para conservar actual) ---

    public static String readOptionalString(String prompt, String currentValue, String example) {
        String p = prompt + " [" + (currentValue != null ? currentValue : "vacio") + "]";
        if (example != null && !example.isEmpty()) {
            p += " (ej: " + example + ")";
        }
        System.out.print(p + ": ");
        String input = SC.nextLine().trim();
        return input.isEmpty() ? currentValue : input;
    }

    public static Integer readOptionalInt(String prompt, Integer currentValue, String example, int min, int max) {
        while (true) {
            String p = prompt + " [" + currentValue + "] [" + min + " - " + max + "]";
            if (example != null && !example.isEmpty()) {
                p += " (ej: " + example + ")";
            }
            System.out.print(p + ": ");
            String input = SC.nextLine().trim();
            if (input.isEmpty()) {
                return currentValue;
            }
            try {
                int val = Integer.parseInt(input);
                if (val < min || val > max) {
                    throw new RangoInvalidoException(prompt, val, min, max, example);
                }
                return val;
            } catch (NumberFormatException e) {
                printError("Debe ingresar un número entero válido o presionar ENTER para mantener el actual.");
            } catch (RangoInvalidoException e) {
                printError(e.getMessage());
            }
        }
    }

    public static BigDecimal readOptionalPositiveBigDecimal(String prompt, BigDecimal currentValue, String example) {
        while (true) {
            String p = prompt + " [" + currentValue + "]";
            if (example != null && !example.isEmpty()) {
                p += " (ej: " + example + ")";
            }
            System.out.print(p + ": ");
            String input = SC.nextLine().trim();
            if (input.isEmpty()) {
                return currentValue;
            }
            try {
                BigDecimal val = new BigDecimal(input);
                if (val.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new RangoInvalidoException(prompt, val, "> 0", "+inf", example);
                }
                return val;
            } catch (NumberFormatException e) {
                printError("Debe ingresar un valor decimal válido o presionar ENTER para mantener el actual.");
            } catch (RangoInvalidoException e) {
                printError(e.getMessage());
            }
        }
    }

    public static LocalDate readOptionalDate(String prompt, LocalDate currentValue, String example) {
        while (true) {
            String currStr = currentValue != null ? currentValue.format(FMT) : "vacio";
            String p = prompt + " [" + currStr + "] (dd/MM/yyyy)";
            if (example != null && !example.isEmpty()) {
                p += " (ej: " + example + ")";
            }
            System.out.print(p + ": ");
            String input = SC.nextLine().trim();
            if (input.isEmpty()) {
                return currentValue;
            }
            try {
                return LocalDate.parse(input, FMT);
            } catch (DateTimeParseException e) {
                printError("Formato de fecha inválido. Use dd/MM/yyyy (ej: " + (example != null ? example : "15/01/2024") + ") o ENTER para conservar.");
            }
        }
    }

    public static <E extends Enum<E>> E readEnum(Class<E> clazz, String prompt) {
        E[] values = clazz.getEnumConstants();
        System.out.println(prompt);
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        int idx = readInt("Seleccione", "1", 1, values.length);
        return values[idx - 1];
    }

    public static boolean readConfirmation(String msg) {
        System.out.print(msg + " (s/n): ");
        return SC.nextLine().trim().toLowerCase().startsWith("s");
    }

    public static int readMenuOption() {
        System.out.print("Seleccione una opcion: ");
        try { return Integer.parseInt(SC.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    // --- Table display ---
    public static void printTable(String[] headers, String[][] rows) {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
            for (String[] row : rows) {
                if (i < row.length && row[i] != null)
                    widths[i] = Math.max(widths[i], row[i].length());
            }
        }
        StringBuilder sb = new StringBuilder();
        StringBuilder sep = new StringBuilder("+");
        for (int w : widths) { sep.append("-".repeat(w + 2)).append("+"); }

        sb.append(sep).append("\n|");
        for (int i = 0; i < headers.length; i++)
            sb.append(" ").append(String.format("%-" + widths[i] + "s", headers[i])).append(" |");
        sb.append("\n").append(sep).append("\n");

        for (String[] row : rows) {
            sb.append("|");
            for (int i = 0; i < headers.length; i++) {
                String val = (i < row.length && row[i] != null) ? row[i] : "";
                sb.append(" ").append(String.format("%-" + widths[i] + "s", val)).append(" |");
            }
            sb.append("\n");
        }
        sb.append(sep);
        System.out.println(sb);
    }
}