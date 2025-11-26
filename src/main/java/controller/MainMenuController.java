package controller;

import controller.user.UserMenuController;
import service.exception.ServiceException;
import service.exception.ValidationException;

import util.ConsoleUtils;
import util.Injector;
import util.Navigation;
import view.MenuRenderer;

public class MainMenuController {

    UserMenuController userMenuController = Injector.createUserMenuController();
    ConfigMenuController adminMenuController = Injector.createConfigMenuController();
    
    public void runMainMenu() {
        Navigation.push("Home");
        while (true) {
            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner("Desktop Game Manager (Home)");
            MenuRenderer.renderOptions(
                "1 - Efetuar (Login/Registro)",
                "2 - Gerenciar Configurações",
                "0 - Sair"
            );
            String option = ConsoleUtils.readString("Escolha: ").trim();

            try {
                switch (option) {
                    case "1":
                        userMenuController.loginOrRegister();
                        break;
                    case "2":
                        adminMenuController.configManagementMenu();
                        break;
                    case "0":
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
