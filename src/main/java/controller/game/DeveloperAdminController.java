package controller.game;

import core.Navigation;
import model.game.Developer;
import service.exception.ServiceException;
import service.exception.ValidationException;
import service.game.DeveloperService;
import util.ConsoleUtils;
import view.DeveloperView;
import view.MenuRenderer;

public class DeveloperAdminController {

    private final DeveloperService developerService;

    public DeveloperAdminController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    public void manageDevelopersMenu() {
        Navigation.push("Developer Management Menu");

        while (true) {
            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner(Navigation.getPath());
            MenuRenderer.renderOptions(
                    "1 - Criar ou Encontrar Desenvolvedor",
                    "2 - Listar Desenvolvedores",
                    "0 - Voltar"
            );
            String option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {
                    case "1":
                        createOrFindDeveloper();
                        ConsoleUtils.waitEnter();
                        break;
                    case "2":
                        DeveloperView.listAll();
                        ConsoleUtils.waitEnter();
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

    public void createOrFindDeveloper() {
        String name = ConsoleUtils.readString("\nNome do desenvolvedor para criar ou encontrar: ");
        Developer dev = developerService.createOrFind(name);
        System.out.println("Desenvolvedor '" + dev.getName() + "' processado com ID: " + dev.getId());
    }
}
