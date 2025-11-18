package controller;

import controller.admin.DeveloperAdminController;
import controller.admin.GameAdminController;
import controller.admin.GenreAdminController;
import controller.admin.PlatformAdminController;
import controller.admin.UserAdminController;
import service.exception.ServiceException;
import service.exception.ValidationException;
import util.ConsoleUtils;
import util.Injector;

public class AdminMenuController {

    private final GameAdminController gameAdminController = Injector.createGameAdminController();
    private final GenreAdminController genreAdminController = Injector.createGenreAdminController();
    private final PlatformAdminController platformAdminController = Injector.createPlatformAdminController();
    private final DeveloperAdminController developerAdminController = Injector.createDeveloperAdminController();
    private final UserAdminController userAdminController = Injector.createUserAdminController();

    public void manageCatalogMenu() throws ServiceException, ValidationException {
        String option;
        do {
            System.out.println("\n--- GERENCIAR CATÁLOGO (ADMIN) ---");
            System.out.println("1 - Gerenciar Jogos");
            System.out.println("2 - Gerenciar Gêneros");
            System.out.println("3 - Gerenciar Plataformas");
            System.out.println("4 - Gerenciar Desenvolvedores");
            System.out.println("5 - Listar Usuários");
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
                case "5":
                    userAdminController.manageUsersMenu();
                case "0":
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (!option.equals("0"));
    }
}
