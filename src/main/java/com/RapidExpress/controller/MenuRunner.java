package com.RapidExpress.controller;

import com.RapidExpress.view.ConsoleUtil;

/**
 * Shared menu runner - eliminates duplicate run() boilerplate across controllers.
 * Each controller passes its menu/read functions and action handlers as lambdas.
 */
public final class MenuRunner {

    private MenuRunner() {}

    /**
     * Runs a standard menu loop until user selects 0 (exit).
     *
     * @param showMenu   Runnable that displays the menu
     * @param readOption Supplier that reads and validates user input
     * @param actions    Varargs of actions matching option 1, 2, 3... in order
     */
    public static void run(Runnable showMenu, java.util.function.IntSupplier readOption, MenuAction... actions) {
        boolean salir = false;
        while (!salir) {
            showMenu.run();
            int opcion = readOption.getAsInt();
            ConsoleUtil.clearScreen();

            if (opcion == 0) {
                salir = true;
            } else if (opcion > 0 && opcion <= actions.length) {
                actions[opcion - 1].execute(opcion);
            } else {
                ConsoleUtil.printError("Opcion no valida.");
            }

            if (!salir && opcion != 0) {
                ConsoleUtil.pressEnterToContinue();
            }
        }
    }

    /**
     * Overload for controllers needing pre-menu setup (e.g., fetching available vehicles/drivers).
     */
    public static void runWithSetup(java.util.function.Supplier<Boolean> setup, Runnable showMenu,
                                     java.util.function.IntSupplier readOption, MenuAction... actions) {
        if (Boolean.FALSE.equals(setup.get())) return;
        run(showMenu, readOption, actions);
    }
}
