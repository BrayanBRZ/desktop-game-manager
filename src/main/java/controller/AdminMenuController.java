package controller;

import service.ServiceException;
import service.ValidationException;

import util.ConsoleUtils;

public class AdminMenuController {

    private final GameAdminController gameAdminController = new GameAdminController();
    private final GenreAdminController genreAdminController = new GenreAdminController();
    private final PlatformAdminController platformAdminController = new PlatformAdminController();
    private final DeveloperAdminController developerAdminController = new DeveloperAdminController();

    protected void manageCatalogMenu() throws ServiceException, ValidationException {
        String option;
        do {
            System.out.println("\n--- GERENCIAR CATÁLOGO (ADMIN) ---");
            System.out.println("1 - Gerenciar Jogos");
            System.out.println("2 - Gerenciar Gêneros");
            System.out.println("3 - Gerenciar Plataformas");
            System.out.println("4 - Gerenciar Desenvolvedores");
            System.out.println("0 - Voltar");
            option = ConsoleUtils.readString("Escolha uma opção: ");

            switch (option) {
                case "1":
                    gameAdminController.manageGamesMenu();
                    break;
                case "2":
                    genreAdminController.manageGenresMenu();
                    break;
                case "3":
                    platformAdminController.managePlatformsMenu();
                    break;
                case "4":
                    developerAdminController.manageDevelopersMenu();
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (!option.equals("0"));
    }
}
