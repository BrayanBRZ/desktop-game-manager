package controller.game;

import core.Navigation;
import model.game.Platform;
import service.exception.ServiceException;
import service.exception.ValidationException;
import service.game.PlatformService;
import utils.ConsoleUtils;
import view.game.PlatformConfigView;

public class PlatformConfigController {

    private final PlatformService platformService;

    public PlatformConfigController(PlatformService platformService) {
        this.platformService = platformService;
    }

    PlatformConfigView platformConfigView = new PlatformConfigView();

    public void managePlatformsMenu() {
        Navigation.push("Platform Management Menu");
        String entityName = "Plataformas";

        while (true) {
            int choice = platformConfigView.renderBanner(
                    "1 - Criar ou Encontrar Plataforma",
                    "2 - Listar Plataformas",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        createOrFind();
                        ConsoleUtils.waitEnter();
                        break;

                    case 2:
                        platformConfigView.displayEntityList(platformService.findAll(), entityName);
                        ConsoleUtils.waitEnter();
                        break;

                    case 0:
                        Navigation.pop();
                        return;

                    default:
                        platformConfigView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                platformConfigView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                platformConfigView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                platformConfigView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    private void createOrFind() {
        String name = ConsoleUtils.readString("\nNome da plataforma para criar ou encontrar: ");
        Platform platform = platformService.createOrFind(name);
        System.out.println("Plataforma '" + platform.getName() + "' processada com ID: " + platform.getId());
    }
}
