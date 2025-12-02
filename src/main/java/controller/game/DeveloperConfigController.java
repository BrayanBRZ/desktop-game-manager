package controller.game;

import model.game.Developer;
import service.exception.ServiceException;
import service.exception.ValidationException;
import service.game.DeveloperService;
import view.game.DeveloperConfigView;
import core.Navigation;
import utils.ConsoleUtils;

public class DeveloperConfigController {

    private final DeveloperService developerService;

    public DeveloperConfigController(DeveloperService developerService) {
        this.developerService = developerService;
    }

    private final DeveloperConfigView developerConfigView = new DeveloperConfigView();

    public void manageDevelopersMenu() {
        Navigation.push("Developer Management Menu");

        while (true) {
            int choice = developerConfigView.renderBanner(
                    "1 - Criar ou Encontrar Desenvolvedor",
                    "2 - Listar Desenvolvedores",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        createOrFind();
                        ConsoleUtils.waitEnter();
                        break;
                    case 2:
                        developerConfigView.renderEntityList(developerService.findAll());
                        ConsoleUtils.waitEnter();
                        break;
                    case 0:
                        Navigation.pop();
                        return;
                    default:
                        developerConfigView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                developerConfigView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                developerConfigView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                developerConfigView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    public void createOrFind() {
        String name = ConsoleUtils.readString("\nNome do desenvolvedor para criar ou encontrar: ", null);
        Developer dev = developerService.createOrFind(name);
        developerConfigView.renderMessageLine("Desenvolvedor '" + dev.getName() + "' processado com ID: " + dev.getId());
    }
}
