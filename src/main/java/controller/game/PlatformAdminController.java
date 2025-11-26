package controller.game;

import model.game.Platform;

import service.PlatformService;
import service.exception.ServiceException;
import service.exception.ValidationException;

import util.ConsoleUtils;
import util.Navigation;

import view.PlatformView;
import view.MenuRenderer;

public class PlatformAdminController {

    private final PlatformService platformService;

    public PlatformAdminController(PlatformService platformService) {
        this.platformService = platformService;
    }

    public void managePlatformsMenu() {
        Navigation.push("Platform Management Menu");

        while (true) {
            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner(Navigation.getPath());
            MenuRenderer.renderOptions(
                    "1 - Criar ou Encontrar Plataforma",
                    "2 - Listar Plataformas",
                    "0 - Voltar"
            );

            String option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {
                    case "1":
                        createOrFindPlatform();
                        ConsoleUtils.waitEnter();
                        break;

                    case "2":
                        PlatformView.listAll();
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

    private void createOrFindPlatform() {
        String name = ConsoleUtils.readString("\nNome da plataforma para criar ou encontrar: ");
        Platform platform = platformService.createOrFind(name);
        System.out.println("Plataforma '" + platform.getName() + "' processada com ID: " + platform.getId());
    }
}
