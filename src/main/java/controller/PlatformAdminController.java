package controller;

import model.Platform;
import service.PlatformService;

import service.ServiceException;
import service.ValidationException;

import util.ConsoleUtils;
import view.PlatformView;

public class PlatformAdminController {

    private final PlatformService platformService = new PlatformService();

    protected void managePlatformsMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- GERENCIAR PLATAFORMAS ---");
        PlatformView.listarPlataformas();
        String name = ConsoleUtils.readString("Nome da plataforma para criar ou encontrar: ");
        Platform platform = platformService.createOrFind(name);
        System.out.println("Plataforma '" + platform.getName() + "' processada com ID: " + platform.getId());
    }
}
