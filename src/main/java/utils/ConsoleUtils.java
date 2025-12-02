package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import model.common.Listable;

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

    public static <T extends Listable> List<Long> selecionarMultiplasEntidades(List<T> entities, String entityName) {
        MenuRenderer.renderMessageLine("\n--- Selecione " + entityName + " (IDs separados por vírgula, ou Enter para manter atuais) ---");

        if (entities.isEmpty()) {
            MenuRenderer.renderMessageLine("Nenhum(a) " + entityName + " cadastrado(a).");
            return new ArrayList<>();
        }

        for (T e : entities) {
            MenuRenderer.renderMessageLine(e.getId() + " - " + e.getName());
        }

        return readListIds("IDs: ");
    }

    private static void cleanIfNumericGarbage() {
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    public static String readString(String prompt) {
        MenuRenderer.renderMessage(prompt);
        return scanner.nextLine().trim();
    }

    public static int readInteger(String prompt) {
        while (true) {
            try {
                MenuRenderer.renderMessage(prompt);
                int value = scanner.nextInt();
                cleanIfNumericGarbage();
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                MenuRenderer.renderMessageLine("ID inválido. Digite um número.");
            }
        }
    }

    public static long readLong(String prompt) {
        while (true) {
            try {
                MenuRenderer.renderMessage(prompt);
                long value = scanner.nextLong();
                cleanIfNumericGarbage();
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                MenuRenderer.renderMessageLine("ID inválido. Digite um número.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            String input = readString(prompt);
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    throw new NumberFormatException();
                }
                return value;
            } catch (NumberFormatException e) {
                MenuRenderer.renderMessageLine("Valor inválido. Digite um número não-negativo.");
            }
        }
    }

    public static LocalDate readData(String prompt, LocalDate defaultValue) {
        String input = readString(prompt);
        if (input.isEmpty()) {
            return defaultValue;
        }

        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            MenuRenderer.renderMessageLine("Data inválida. Retornando valor padrão.");
            return defaultValue;
        }
    }

    public static LocalDateTime readDataHora(String prompt, LocalDateTime defaultValue) {
        String input = readString(prompt);
        if (input.isEmpty()) {
            return defaultValue;
        }

        try {
            return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            MenuRenderer.renderMessageLine("Data/hora inválida. Retornando valor padrão.");
            return defaultValue;
        }
    }

    public static List<Long> readListIds(String prompt) {
        String input = readString(prompt);
        if (input.isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(input.split(","))
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
                .collect(Collectors.toList());
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
