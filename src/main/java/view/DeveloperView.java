package view;

import model.Developer;
import service.DeveloperService;

import service.ServiceException;

import java.util.List;

public class DeveloperView {
    
    private static final DeveloperService developerService = new service.DeveloperService();

    public static void listarDesenvolvedores() throws ServiceException {
        List<Developer> devs = developerService.findAll();
        System.out.println("\n--- DESENVOLVEDORES DISPONÍVEIS ---");
        if (devs.isEmpty()) {
            System.out.println("Nenhum desenvolvedor cadastrado.");
        } else {
            devs.forEach(d -> System.out.println("ID: " + d.getId() + " - " + d.getName()
                    + (d.getLocation() != null ? " (" + d.getLocation() + ")" : "")));
        }
    }
}
