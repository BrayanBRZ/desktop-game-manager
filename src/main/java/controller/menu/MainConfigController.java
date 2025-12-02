package controller.menu;

import service.exception.ServiceException;
import service.exception.ValidationException;
import controller.game.*;
import controller.user.UserConfigController;
import view.MainMenuView;
import core.Injector;
import core.Navigation;
import utils.ConsoleUtils;

public class MainConfigController {

    private final GameConfigController gameConfigController = Injector.createGameConfigController();
    private final GenreConfigController genreConfigController = Injector.createGenreConfigController();
    private final PlatformConfigController platformMenuController = Injector.createPlatformMenuController();
    private final DeveloperConfigController developerConfigController = Injector.createDeveloperConfigController();
    private final UserConfigController userConfigController = Injector.createUserConfigController();
    private final SeederConfigController seederConfigController = Injector.createSeederConfigController();

    private final MainMenuView configView = new MainMenuView();

    public void configManagementMenu() {
        Navigation.push("Config Management Menu");

        while (true) {
            int choice = configView.renderBanner(
                    "1 - Gerenciar Jogos",
                    "2 - Gerenciar Gêneros",
                    "3 - Gerenciar Plataformas",
                    "4 - Gerenciar Desenvolvedores",
                    "5 - Gerenciar Usuários",
                    "6 - Semear Ambiente",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        gameConfigController.manageGamesMenu();
                        break;
                    case 2:
                        genreConfigController.manageGenresMenu();
                        break;
                    case 3:
                        platformMenuController.managePlatformsMenu();
                        break;
                    case 4:
                        developerConfigController.manageDevelopersMenu();
                        break;
                    case 5:
                        userConfigController.manageUsersMenu();
                        break;
                    case 6:
                        seederConfigController.seedersManagementMenu();
                        break;
                    case 0:
                        Navigation.pop();
                        return;
                    default:
                        configView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                configView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                configView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                configView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }
}
