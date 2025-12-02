package controller.game;

import model.game.Platform;
import service.game.PlatformService;
import service.exception.ServiceException;
import service.exception.ValidationException;
import view.game.PlatformConfigView;
import core.Navigation;
import utils.ConsoleUtils;

public class PlatformConfigController {

    private final PlatformService platformService;

    public PlatformConfigController(PlatformService platformService) {
        this.platformService = platformService;
    }

    PlatformConfigView platformConfigView = new PlatformConfigView();

    public void managePlatformsMenu() {
        Navigation.push("Platform Management Menu");

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
                        platformConfigView.renderEntityList(platformService.findAll());
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
        String name = ConsoleUtils.readString("\nNome da plataforma para criar ou encontrar: ", null);
        Platform platform = platformService.createOrFind(name);
        platformConfigView.renderMessageLine("Plataforma '" + platform.getName() + "' processada com ID: " + platform.getId());
    }
}
