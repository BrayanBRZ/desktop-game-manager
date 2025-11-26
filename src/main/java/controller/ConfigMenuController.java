package controller;

import controller.game.DeveloperAdminController;
import controller.game.GameAdminController;
import controller.game.GenreAdminController;
import controller.game.PlatformAdminController;
import controller.user.UserAdminController;
import service.exception.ServiceException;
import service.exception.ValidationException;

import util.ConsoleUtils;
import util.Injector;
import util.Navigation;
import view.MenuRenderer;

public class ConfigMenuController {

    private final GameAdminController gameAdminController = Injector.createGameAdminController();
    private final GenreAdminController genreAdminController = Injector.createGenreAdminController();
    private final PlatformAdminController platformMenuController = Injector.createPlatformMenuController();
    private final DeveloperAdminController developerAdminController = Injector.createDeveloperAdminController();
    private final UserAdminController userAdminController = Injector.createUserAdminController();

    public void configManagementMenu() {
        Navigation.push("Config Management Menu");
        while (true) {
            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner(Navigation.getPath());
            MenuRenderer.renderOptions(
                    "1 - Gerenciar Jogos",
                    "2 - Gerenciar Gêneros",
                    "3 - Gerenciar Plataformas",
                    "4 - Gerenciar Desenvolvedores",
                    "5 - Gerenciar Usuários",
                    "0 - Voltar"
            );
            String option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {
                    case "1":
                        gameAdminController.manageGamesMenu();
                        break;
                    case "2":
                        genreAdminController.manageGenresMenu();
                        break;
                    case "3":
                        platformMenuController.managePlatformsMenu();
                        break;
                    case "4":
                        developerAdminController.manageDevelopersMenu();
                        break;
                    case "5":
                        userAdminController.manageUsersMenu();
                        break;
                    case "0":
                        Navigation.pop();
                        return;
                    default:
                        MenuRenderer.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                MenuRenderer.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                MenuRenderer.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                MenuRenderer.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }
}
