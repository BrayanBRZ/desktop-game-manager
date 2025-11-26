package view;

import service.exception.ServiceException;
import service.game.DeveloperService;

import java.util.List;

import model.game.Developer;

public class DeveloperView {
    
    private static final DeveloperService developerService = new service.game.DeveloperService();

    public static void listAll() throws ServiceException {
        List<Developer> devs = developerService.findAll();
        System.out.println("\n[ DESENVOLVEDORES DISPONÍVEIS ]");
        if (devs.isEmpty()) {
            System.out.println("Nenhum desenvolvedor cadastrado.");
        } else {
            devs.forEach(d -> System.out.println("ID: " + d.getId() + " - " + d.getName()));
        }
    }
}
