package view;

import service.PlatformService;
import service.exception.ServiceException;

import java.util.List;

import model.game.Platform;

public class PlatformView {

    private static final PlatformService platformService = new PlatformService();
    
    public static void listAll() throws ServiceException {
        List<Platform> platforms = platformService.findAll();
        System.out.println("\n--- PLATAFORMAS DISPONÍVEIS ---");
        if (platforms.isEmpty()) {
            System.out.println("Nenhuma plataforma cadastrada.");
        } else {
            platforms.forEach(p -> System.out.println("ID: " + p.getId() + " - " + p.getName()));
        }
    }
}
