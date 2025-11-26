package controller.user;

import model.user.User;
import service.UserService;
import service.exception.ServiceException;
import service.exception.ValidationException;

import util.ConsoleUtils;
import util.Navigation;

import view.MenuRenderer;
import view.UserView;

import java.time.LocalDate;

import service.AuthService;

public class UserAdminController {

    private final UserService userService;
    private final AuthService authService;

    public UserAdminController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    public void manageUsersMenu() {
        Navigation.push("User Management Menu");

        while (true) {
            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner(Navigation.getPath());
            MenuRenderer.renderOptions(
                    "1 - Criar Usuário",
                    "2 - Atualizar Usuário",
                    "3 - Deletar Usuário (requer senha)",
                    "4 - Listar Usuários",
                    "0 - Voltar"
            );

            String option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {
                    case "1":
                        createUser();
                        ConsoleUtils.waitEnter();
                        break;

                    case "2":
                        updateUser();
                        ConsoleUtils.waitEnter();
                        break;

                    case "3":
                        deleteUser();
                        ConsoleUtils.waitEnter();
                        break;

                    case "4":
                        UserView.listAll();
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

    private void createUser() throws ServiceException, ValidationException {
        System.out.println("\n[ CRIAR USUÁRIO ]");

        String name = ConsoleUtils.readString("Nome: ");
        String password = ConsoleUtils.readString("Nova senha: ");

        User created = authService.register(name, password);

        System.out.println("Usuário '" + created.getName() + "' criado com ID: " + created.getId());
    }

    private void updateUser() throws ServiceException, ValidationException {
        System.out.println("\n[ ATUALIZAR USUÁRIO ]");

        Long id = ConsoleUtils.readLong("ID do usuário: ");
        User user = userService.findById(id);
        if (user == null) {
            MenuRenderer.renderError("Usuário não encontrado.");
            return;
        }

        System.out.println("Editando usuário: " + user.getName());

        String newName = ConsoleUtils.readString("Novo nome (Enter para manter '" + user.getName() + "'): ");
        if (newName.isEmpty()) {
            newName = user.getName();
        }

        LocalDate newBirthDate = ConsoleUtils.readData(
                "Nova data de nascimento (Enter para manter atual): ",
                user.getBirthDate());

        if (!passIsValid()) {
            return;
        }

        User updated = userService.updateUserProfile(
                id, newName, newBirthDate
        );

        System.out.println("Usuário '" + updated.getName() + "' atualizado com sucesso!");
    }

    private void deleteUser() throws ServiceException {
        System.out.println("\n[ DELETAR USUÁRIO ]");

        Long id = ConsoleUtils.readLong("ID do usuário: ");
        User user = userService.findById(id);

        if (user == null) {
            MenuRenderer.renderError("Usuário não encontrado.");
            return;
        }

        if (!passIsValid()) {
            return;
        }

        userService.deleteUser(id);
        System.out.println("Usuário '" + user.getName() + "' deletado com sucesso.");
    }

    private boolean passIsValid() {
        String password = ConsoleUtils.readString("Digite a senha MASTER para confirmar a exclusão: ");

        if (!password.equals("admin123")) {
            MenuRenderer.renderError("Senha incorreta. Operação cancelada.");
            return false;
        }
        return true;
    }
}
