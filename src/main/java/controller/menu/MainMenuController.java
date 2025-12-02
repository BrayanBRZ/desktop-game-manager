package controller.menu;

import controller.user.UserMenuController;
import core.Injector;
import core.Navigation;
import service.exception.ServiceException;
import service.exception.ValidationException;
import utils.ConsoleUtils;
import view.MainMenuView;

public class MainMenuController {

    private final UserMenuController userMenuController = Injector.createUserMenuController();
    private final MainConfigController adminMenuController = Injector.createConfigMenuController();

    private final MainMenuView mainMenuView = new MainMenuView();

    public void runMainMenu() {
        Navigation.push("Home");

        while (true) {
            int choice = mainMenuView.renderBanner(
                    "1 - Efetuar (Login/Registro)",
                    "2 - Gerenciar Configurações",
                    "0 - Sair"
            );

            try {
                switch (choice) {
                    case 1:
                        userMenuController.loginOrRegister();
                        break;
                    case 2:
                        adminMenuController.configManagementMenu();
                        break;
                    case 0:
                        return;
                    default:
                        mainMenuView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                mainMenuView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                mainMenuView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                mainMenuView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }
}
