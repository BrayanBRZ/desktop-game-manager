package controller.user;

import model.user.User;
import service.session.AuthService;
import service.user.UserService;
import service.exception.ServiceException;
import service.exception.ValidationException;
import view.user.UserConfigView;
import core.Navigation;
import dto.UserDTO;
import utils.ConsoleUtils;

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
                        userService.passIsValid();
                        updateUser();
                        ConsoleUtils.waitEnter();
                        break;

                    // case 3:
                    //     userService.passIsValid();
                    //     deleteUser();
                    //     ConsoleUtils.waitEnter();
                    //     break;
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

    private void createUser() {
        userConfigView.renderMessageLine("[ CRIAR USUÁRIO ]");

        UserDTO dto = userConfigView.promptUserCreate();
        User created = authService.register(
                dto.getName(),
                dto.getPassword()
        );

        userConfigView.renderMessageLine("Usuário '" + created.getName() + "' criado com ID: " + created.getId());
    }

    private void updateUser() {
        userConfigView.renderMessageLine("[ ATUALIZAR USUÁRIO ]");
        Long id = userConfigView.readLong("ID do usuário: ");
        User user = userService.findById(id);

        UserDTO dto = userConfigView.promptUserUpdate(user);
        User updated = userService.updateUserProfile(
                dto.getId(),
                dto.getName(),
                dto.getBirthDate()
        );

        userConfigView.renderMessageLine("Usuário '" + updated.getName() + "' atualizado com sucesso!");
    }

    // Desativado pois ainda necessita implementar delete da relação de amizade para efetuar ação
    // private void deleteUser() {
    //     userConfigView.renderMessageLine("\n[ DELETAR USUÁRIO ]");
    //     Long id = ConsoleUtils.readLong("ID do usuário: ");
    //     if (!passIsValid()) return;
    //     userService.deleteUser(id);
    //     userConfigView.renderMessageLine("Usuário deletado com sucesso.");
    // }
}
