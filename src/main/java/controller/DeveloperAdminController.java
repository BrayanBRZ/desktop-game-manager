package controller;

import model.Developer;
import service.DeveloperService;

import service.ServiceException;
import service.ValidationException;

import util.ConsoleUtils;
import view.DeveloperView;

public class DeveloperAdminController {

    private final DeveloperService developerService = new DeveloperService();

    protected void manageDevelopersMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- GERENCIAR DESENVOLVEDORES ---");
        DeveloperView.listarDesenvolvedores();
        String name = ConsoleUtils.readString("Nome do desenvolvedor para criar ou encontrar: ");
        String location = ConsoleUtils.readString("Localização (Enter para vazio): ");
        String symbolPath = ConsoleUtils.readString("Caminho do símbolo (Enter para vazio): ");
        Developer dev = developerService.createOrFind(name);
        // Atualiza dados extras se necessário
        if (!location.isEmpty() || !symbolPath.isEmpty()) {
            dev = developerService.updateDeveloper(dev.getId(), name, location.isEmpty() ? null : location,
                    symbolPath.isEmpty() ? null : symbolPath);
        }
        System.out.println("Desenvolvedor '" + dev.getName() + "' processado com ID: " + dev.getId());
    }
}
