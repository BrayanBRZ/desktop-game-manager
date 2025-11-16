package controller;

import service.ServiceException;
import service.ValidationException;

import util.ConsoleUtils;

public class MainMenuController {

    private final UserMenuController userMenuController = new UserMenuController();
    private final AdminMenuController adminMenuController = new AdminMenuController();

    public void runMainMenu() {
        String option;
        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Acessar Biblioteca (Login/Registro)");
            System.out.println("2 - Gerenciar Catálogo (Admin)");
            System.out.println("0 - Sair");
            option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {
                    case "1":
                        userMenuController.loginOrRegister();
                        break;
                    case "2":
                        adminMenuController.manageCatalogMenu();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (ServiceException | ValidationException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        } while (!option.equals("0"));
    }
}
