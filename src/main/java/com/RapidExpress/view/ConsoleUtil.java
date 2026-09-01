package com.RapidExpress.view;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.function.IntSupplier;

/**
 * Shared console I/O. All static, no state.
 * ponytail: one Scanner instance, not per-call. Replace with java.io.Console if you want raw TTY.
 */
public final class ConsoleUtil {

    private static final Scanner SC = new Scanner(System.in);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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

    // --- Input ---
    public static String readString(String prompt) {
        System.out.print(prompt + ": ");
        return SC.nextLine().trim();
    }

    public static String readRequiredString(String prompt) {
        String s;
        do { s = readString(prompt); } while (s.isEmpty());
        return s;
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try { return Integer.parseInt(SC.nextLine().trim()); }
            catch (NumberFormatException e) { printError("Ingrese un numero valido."); }
        }
    }

    public static int readInt(String prompt, int min, int max) {
        int v;
        do { v = readInt(prompt); } while (v < min || v > max);
        return v;
    }

    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            try { return new BigDecimal(SC.nextLine().trim()); }
            catch (NumberFormatException e) { printError("Ingrese un numero valido."); }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (dd/MM/yyyy): ");
            try { return LocalDate.parse(SC.nextLine().trim(), FMT); }
            catch (DateTimeParseException e) { printError("Fecha invalida. Use dd/MM/yyyy."); }
        }
    }

    public static <E extends Enum<E>> E readEnum(Class<E> clazz, String prompt) {
        E[] values = clazz.getEnumConstants();
        System.out.println(prompt);
        for (int i = 0; i < values.length; i++) {
            System.out.println("  " + (i + 1) + ". " + values[i]);
        }
        int idx = readInt("Seleccione", 1, values.length);
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