package controller.user;

import model.user.User;

import service.exception.ServiceException;
import service.exception.ValidationException;

import service.session.AuthService;
import service.user.UserService;
import utils.ConsoleUtils;

import java.time.LocalDate;

import static core.AppConfig.ADMIN_PASSWORD;

import core.Navigation;
import view.user.UserConfigView;

public class UserConfigController {

    private final UserService userService;
    private final AuthService authService;

    public UserConfigController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    private final UserConfigView userConfigView = new UserConfigView();

    public void manageUsersMenu() {
        Navigation.push("User Management Menu");

        while (true) {
            int choice = userConfigView.renderBanner(
                    "1 - Criar Usuário",
                    "2 - Atualizar Usuário",
                    "3 - Deletar Usuário (requer senha)",
                    "4 - Listar Usuários",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        createUser();
                        ConsoleUtils.waitEnter();
                        break;

                    case 2:
                        updateUser();
                        ConsoleUtils.waitEnter();
                        break;

                    case 3:
                        deleteUser();
                        ConsoleUtils.waitEnter();
                        break;

                    case 4:
                        userConfigView.listAllUsers(userService.findAll());
                        ConsoleUtils.waitEnter();
                        break;

                    case 0:
                        Navigation.pop();
                        return;

                    default:
                        userConfigView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }

            } catch (ValidationException e) {
                userConfigView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                userConfigView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                userConfigView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    private void createUser() throws ServiceException, ValidationException {
        userConfigView.renderMessageLine("\n[ CRIAR USUÁRIO ]");
        String name = ConsoleUtils.readString("Nome: ");
        String password = ConsoleUtils.readString("Nova senha: ");
        User created = authService.register(name, password);
        userConfigView.renderMessageLine("Usuário '" + created.getName() + "' criado com ID: " + created.getId());
    }

    private void updateUser() throws ServiceException, ValidationException {
        userConfigView.renderMessageLine("\n[ ATUALIZAR USUÁRIO ]");
        Long id = ConsoleUtils.readLong("ID do usuário: ");
        User user = userService.findById(id);
        if (user == null) {
            throw new ValidationException("Usuário não encontrado.");
        }

        userConfigView.renderMessageLine("Editando usuário: " + user.getName());

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

        userConfigView.renderMessageLine("Usuário '" + updated.getName() + "' atualizado com sucesso!");
    }

    private void deleteUser() throws ServiceException {
        userConfigView.renderMessageLine("\n[ DELETAR USUÁRIO ]");

        Long id = ConsoleUtils.readLong("ID do usuário: ");
        User user = userService.findById(id);

        if (user == null) {
            userConfigView.renderError("Usuário não encontrado.");
            return;
        }

        if (!passIsValid()) {
            return;
        }

        userService.deleteUser(id);
        userConfigView.renderMessageLine("Usuário '" + user.getName() + "' deletado com sucesso.");
    }

    private boolean passIsValid() {
        String password = ConsoleUtils.readString("Digite a senha MASTER para confirmar a exclusão: ");

        if (!password.equals(ADMIN_PASSWORD)) {
            userConfigView.renderError("Senha incorreta. Operação cancelada.");
            return false;
        }
        return true;
    }
}
