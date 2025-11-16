package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class ConsoleUtils {

    private static final Scanner scanner = new Scanner(System.in);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // --- MÉTODOS AUXILIARES ---
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

    public static <T> List<Long> selecionarMultiplasEntidades(List<T> entities, String entityName) {
        System.out.println(
                "\n--- Selecione " + entityName + " (IDs separados por vírgula, ou Enter para manter atuais) ---");
        if (entities.isEmpty()) {
            System.out.println("Nenhum(a) " + entityName + " cadastrado(a).");
            return new ArrayList<>();
        }
        entities.forEach(e -> {
            try {
                Long id = (Long) e.getClass().getMethod("getId").invoke(e);
                String name = (String) e.getClass().getMethod("getName").invoke(e);
                System.out.println("ID: " + id + " - " + name);
            } catch (Exception ex) {
                // Ignora
            }
        });
        return readListaIds("IDs: ");
    }

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static long readLong(String prompt) {
        while (true) {
            try {
                return Long.parseLong(readString(prompt).trim());
            } catch (NumberFormatException e) {
                System.out.println("ID inválido. Digite um número.");
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
                System.out.println("Valor inválido. Digite um número não-negativo.");
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
            System.out.println("Data inválida. Retornando valor padrão.");
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
            System.out.println("Data/hora inválida. Retornando valor padrão.");
            return defaultValue;
        }
    }

    public static List<Long> readListaIds(String prompt) {
        String input = readString(prompt);
        if (input.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido ignorado: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
