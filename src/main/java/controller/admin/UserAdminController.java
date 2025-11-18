package controller.admin;

import service.UserService;
import service.exception.ServiceException;
import service.exception.ValidationException;

// import util.ConsoleUtils;
// import view.UserView;

public class UserAdminController {

    private final UserService userService;

    public UserAdminController(UserService userService) {
        this.userService = userService;
    }

    // Levar para "View"
    public void manageUsersMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- LISTA DE USUÁRIOS ---");
        try {
            userService.findAll().forEach(user -> {
                System.out.println("ID: " + user.getId() + ", Nome: " + user.getName());
            });
        } catch (ServiceException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }
    }
}
