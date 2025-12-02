package utils;

import model.common.Listable;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class ConsoleUtils {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "N/A";
        }
        return date.format(DATE_FORMATTER);
    }

    public static <T extends Listable> MyLinkedList<Long> selecionarMultiplasEntidades(MyLinkedList<T> entities, String entityName) {
        MenuRenderer.renderMessageLine("\n--- Selecione " + entityName + " (IDs separados por vírgula, ou Enter para manter atuais) ---");

        if (entities.isEmpty()) {
            MenuRenderer.renderMessageLine("Nenhum(a) " + entityName + " cadastrado(a).");
            return new MyLinkedList<>();
        }

        for (T e : entities) {
            MenuRenderer.renderMessageLine(e.getId() + " - " + e.getName());
        }

        return readListIds("IDs: ");
    }

    // private static void cleanIfNumericGarbage() {
    //     if (scanner.hasNextLine()) {
    //         scanner.nextLine();
    //     }
    // }
    
    public static String readString(String prompt, String defaultValue) {
        MenuRenderer.renderMessage(prompt);
        String input = scanner.nextLine().trim();

        if (input.isEmpty() && defaultValue != null) {
            return defaultValue;
        }
        return input;
    }

    public static int readInteger(String prompt, String defaultValue) {
        while (true) {
            String input = readString(prompt, defaultValue);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                MenuRenderer.renderMessageLine("Valor inválido. Digite um número.");
            }
        }
    }

    public static long readLong(String prompt, String defaultValue) {
        while (true) {
            String input = readString(prompt, defaultValue);
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                MenuRenderer.renderMessageLine("Valor inválido. Digite um número.");
            }
        }
    }

    public static double readDouble(String prompt, String defaultValue) {
        while (true) {
            String input = readString(prompt, defaultValue);
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                MenuRenderer.renderMessageLine("Valor inválido. Digite um número.");
            }
        }
    }

    public static LocalDate readData(String prompt, String defaultValue) {
        String input = readString(prompt, defaultValue);
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            MenuRenderer.renderMessageLine("Data inválida. Retornando valor padrão.");
            return LocalDate.parse(defaultValue, DATE_FORMATTER);
        }
    }

    public static LocalDateTime readDataHora(String prompt, String defaultValue) {
        String input = readString(prompt, defaultValue);
        try {
            return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            MenuRenderer.renderMessageLine("Data/hora inválida. Retornando valor padrão.");
            return LocalDateTime.parse(defaultValue, DATE_TIME_FORMATTER);
        }
    }

    public static MyLinkedList<Long> readListIds(String prompt) {
        String input = readString(prompt, null);
        if (input.isEmpty()) {
            return new MyLinkedList<>();
        }

        return MyLinkedList.fromJavaList(
                Arrays.stream(input.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(s -> {
                            try {
                                return Long.valueOf(s);
                            } catch (NumberFormatException e) {
                                MenuRenderer.renderMessageLine("ID inválido ignorado: " + s);
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
    }

    public static void waitEnter() {
        MenuRenderer.renderMessageLine("\nPressione ENTER para continuar...");
        scanner.nextLine();
    }

    public static void clearScreen() {
        MenuRenderer.renderMessage("\033[H\033[2J");
        System.out.flush();
    }
}
