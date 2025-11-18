package controller;

import service.exception.ServiceException;
import service.exception.ValidationException;
import util.ConsoleUtils;
import util.Injector;

public class MainMenuController {

    UserMenuController userMenuController = Injector.createUserMenuController();
    AdminMenuController adminMenuController = new AdminMenuController();

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
                        userMenuController.start();
                        break;
                    case "2":
                        adminMenuController.manageCatalogMenu();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (ValidationException e) {
                System.out.println("Erro de validação: " + e.getMessage());
            } catch (ServiceException e) {
                System.out.println("Erro no serviço: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        } while (!option.equals("0"));
    }
}
