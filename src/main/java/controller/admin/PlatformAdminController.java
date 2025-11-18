package controller.admin;

import model.game.Platform;
import service.PlatformService;
import service.exception.ServiceException;
import service.exception.ValidationException;
import util.ConsoleUtils;
import view.PlatformView;

public class PlatformAdminController {

    private final PlatformService platformService;

    public PlatformAdminController(PlatformService platformService) {
        this.platformService = platformService;
    }

    public void managePlatformsMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- GERENCIAR PLATAFORMAS ---");
        PlatformView.listarPlataformas();
        String name = ConsoleUtils.readString("Nome da plataforma para criar ou encontrar: ");
        Platform platform = platformService.createOrFind(name);
        System.out.println("Plataforma '" + platform.getName() + "' processada com ID: " + platform.getId());
    }
}
