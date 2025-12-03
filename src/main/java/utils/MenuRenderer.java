package utils;

import service.exception.ServiceException;
import service.exception.ValidationException;

public final class MenuRenderer {

    //       _____           ___           ___     
    //      /  \  \         /\  \         /\__\    
    //     / /\ \  \       /  \  \       /  |  |   
    //    / /  \ \  \     / /\ \  \     / | |  |   
    //   / /  / \ \__\   / /  \ \  \   / /| |__|__ 
    //  / /__/  | |__|  / /__/_\ \__\ / / |__  \__\
    //  \ \  \ / /  /   \ \  /\ \/__/ \/__/  / /  /
    //   \ \  / /  /     \ \ \ \__\         / /  / 
    //    \ \/ /  /       \ \/ /  /        / /  /  
    //     \__/__/         \__/__/        /_/__/   
    public static void renderBanner(String appName) {
        String[] banner = new String[]{
            "       _____           ___           ___     ",
            "      /  \\  \\         /\\  \\         /\\__\\    ",
            "     / /\\ \\  \\       /  \\  \\       /  |  |   ",
            "    / /  \\ \\  \\     / /\\ \\  \\     / | |  |   ",
            "   / /  / \\ \\__\\   / /  \\ \\  \\   / /| |__|__ ",
            "  / /__/  | |__|  / /__/_\\ \\__\\ / / |__  \\__\\",
            "  \\ \\  \\ / /  /   \\ \\  /\\ \\/__/ \\/__/  / /  /",
            "   \\ \\  / /  /     \\ \\ \\ \\__\\         / /  / ",
            "    \\ \\/ /  /       \\ \\/ /  /        / /  /  ",
            "     \\__/__/         \\__/__/        /_/__/   "
        };

        System.out.println();
        for (String line : banner) {
            System.out.println(line);
        }
        System.out.println();
        System.out.println(centerText(appName, 45));
        System.out.println();
    }

    private static String centerText(String text, int width) {
        if (text == null) {
            text = "";
        }
        if (text.length() >= width) {
            return text;
        }
        int pad = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pad; i++) {
            sb.append(' ');
        }
        sb.append(text);
        return sb.toString();
    }

    public static void renderOptions(String... options) {
        for (String opt : options) {
            System.out.println(opt);
        }
    }

    public static void renderMessage(String msg) {
        System.out.print(msg);
    }
    public static void renderMessageLine(String msg) {
        System.out.println(msg);
    }

    public static void renderError(String msg) {
        System.out.println("\u001B[33mErro: " + msg + "\u001B[0m");
    }

    public static void renderValidationException(ValidationException e) {
        System.out.println("\u001B[33mErro de validação: " + e.getMessage() + "\u001B[0m");
    }

    public static void renderServiceException(ServiceException e) {
        System.out.println("\u001B[33mErro no serviço: " + e.getMessage() + "\u001B[0m");
    }
    public static void renderException(Exception e) {
        System.out.println("\u001B[33mErro inesperado: " + e.getMessage() + "\u001B[0m");
    }
}
