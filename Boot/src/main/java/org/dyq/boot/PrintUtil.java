package org.dyq.boot;

import picocli.CommandLine;

public class PrintUtil {
    public static void error(String log) {
        print(log, "red", false);
    }

    public static void errorLine(String log) {
        print(log, "red", true);
    }

    public static void info(String log) {
        print(log, "magenta", false);
    }

    public static void infoLine(String log) {
        print(log, "magenta", true);
    }

    public static void warn(String log) {
        print(log, "yellow", false);
    }

    public static void warnLine(String log) {
        print(log, "yellow", true);
    }

    public static void success(String log) {
        print(log, "green", false);
    }

    public static void successLine(String log) {
        print(log, "green", true);
    }

    private static void print(String log, String fg, boolean newLine) {
        String print = CommandLine.Help.Ansi.AUTO.string(String.format("@|fg(%s) %s|@", fg, log));
        if (newLine) {
            System.out.println(print);
        } else {
            System.out.print(print);
        }
    }
}
