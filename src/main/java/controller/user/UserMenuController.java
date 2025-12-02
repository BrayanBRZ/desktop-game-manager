package controller.user;

import model.user.User;
import model.user.UserGame;
import service.session.AuthService;
import service.session.SessionManager;
import service.user.FriendshipService;
import service.user.UserGameService;
import service.user.UserService;
import service.exception.ServiceException;
import service.exception.ValidationException;
import view.user.UserMenuView;
import view.user.UserMenuView.UserGameUpdateDTO;
import view.user.UserMenuView.UserProfileUpdateDTO;
import core.Injector;
import core.Navigation;
import utils.ConsoleUtils;

public class UserMenuController {

    private final FriendMenuController friendMenuController = Injector.createFriendMenuController();

    private final AuthService authService;
    private final UserService userService;
    private final UserGameService userGameService;

    public UserMenuController(AuthService authService, UserService userService,
            FriendshipService friendshipService, UserGameService userGameService) {
        this.authService = authService;
        this.userService = userService;
        this.userGameService = userGameService;
    }

    private final UserMenuView userMenuView = new UserMenuView();

    public void loginOrRegister() {
        ConsoleUtils.clearScreen();

        userMenuView.renderMessageLine("[ LOGIN / REGISTRO ]");
        String name = ConsoleUtils.readString("Nome de usuário: ",null).trim();

        User user = userService.findByName(name);

        if (user == null) { // Usuario nao existe
            String answer = ConsoleUtils.readString("Usuário não encontrado. Deseja criar uma conta? (s/n): ", null).trim();

            if (!answer.equalsIgnoreCase("s")) {
                return;
            }

            String password = ConsoleUtils.readString("Nova senha: ", null);

            try {
                user = authService.register(name, password);
                userMenuView.renderMessageLine("Conta criada com sucesso!");
            } catch (ValidationException e) {
                userMenuView.renderMessageLine("Erro ao registrar: " + e.getMessage());

                return;
            }
        } else { // Usuario existe
            String password = ConsoleUtils.readString("Senha: ", null);

            try {
                user = authService.login(name, password);
            } catch (ValidationException e) {
                userMenuView.renderMessageLine("Credenciais inválidas.");

                return;
            }
        }

        // Sucesso
        SessionManager.login(user);
        userMenuView.renderMessageLine("Bem-vindo(a), " + user.getName() + "!");

        runMainMenu();
    }

    private void runMainMenu() {
        ConsoleUtils.clearScreen();
        Navigation.push("User Menu");

        while (true) {
            SessionManager.login(userService.findById(SessionManager.getCurrentUserId()));
            int choice = userMenuView.renderBanner(
                    "1 - Minha biblioteca",
                    "2 - Adicionar jogo",
                    "3 - Remover jogo",
                    "4 - Atualizar progresso de jogo",
                    "5 - Visualizar perfil",
                    "6 - Atualizar perfil",
                    "7 - Alterar senha",
                    "8 - Amizades",
                    "0 - Logout"
            );

            try {
                switch (choice) {

                    case 1:
                        showLibrary();
                        ConsoleUtils.waitEnter();
                        break;
                    case 2:
                        addGameToLibrary();
                        ConsoleUtils.waitEnter();
                        break;
                    case 3:
                        removeGameFromLibrary();
                        ConsoleUtils.waitEnter();
                        break;
                    case 4:
                        updateGameProgress();
                        ConsoleUtils.waitEnter();
                        break;
                    case 5:
                        userMenuView.showProfile(SessionManager.getCurrentUser());
                        ConsoleUtils.waitEnter();
                        break;
                    case 6:
                        updateProfile();
                        ConsoleUtils.waitEnter();
                        break;
                    case 7:
                        changePassword();
                        ConsoleUtils.waitEnter();
                        break;
                    case 8:
                        friendMenuController.friendshipMenu();
                        break;
                    case 0: {
                        SessionManager.logout();
                        Navigation.pop();
                        userMenuView.renderMessageLine("Logout realizado com sucesso.");
                        ConsoleUtils.waitEnter();
                        return;
                    }
                    default:
                        userMenuView.renderError("Opção inválida.");
                }
            } catch (ValidationException e) {
                userMenuView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                userMenuView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                userMenuView.renderException(e);
                ConsoleUtils.waitEnter();
            }

        }
    }

    // #region User Library Management
    private void showLibrary() {
        User user = SessionManager.getCurrentUser();
        userMenuView.showLibrary(user.getUserGames());
    }

    private void addGameToLibrary() throws ServiceException, ValidationException {
        userMenuView.renderMessageLine("\n[ ADICIONAR JOGO ]");
        String gameName = ConsoleUtils.readString("Nome do jogo: ", null);
        userService.addGameToLibrary(SessionManager.getCurrentUserId(), gameName);
        userMenuView.renderMessageLine("Jogo adicionado com sucesso!");
    }

    private void removeGameFromLibrary() throws ServiceException, ValidationException {
        Long gameId = ConsoleUtils.readLong("ID do jogo para remover: ", null);
        userService.removeGameFromLibrary(SessionManager.getCurrentUserId(), gameId);
        userMenuView.renderMessageLine("Jogo removido com sucesso!");
    }

    public void updateGameProgress() {
        Long gameId = ConsoleUtils.readLong("ID do jogo: ", null);

        UserGame userGame = userGameService.findByUserAndGame(
                SessionManager.getCurrentUserId(), gameId
        );

        if (userGame == null) {
            throw new ValidationException("Jogo não encontrado na sua biblioteca.");
        }

        UserGameUpdateDTO dto = userMenuView.promptUpdateGameProgress(userGame);

        if (dto == null) {
            return;
        }

        userGameService.updateAllAttributes(
                dto.userId,
                dto.gameId,
                dto.estimated,
                dto.state,
                dto.hours,
                dto.lastPlayed
        );

        userMenuView.renderMessageLine("Progresso atualizado!");
    }
    // #endregion User Library Management

    // #region User Profile Management
    public void updateProfile() throws ServiceException, ValidationException {
        User user = SessionManager.getCurrentUser();

        UserProfileUpdateDTO dto = userMenuView.promptProfileUpdate(user);
        userService.updateUserProfile(dto.userId, dto.name, dto.birthDate);
        userMenuView.renderMessageLine("Perfil atualizado!");

        SessionManager.login(userService.findById(dto.userId));
    }

    private void changePassword() throws ServiceException, ValidationException {
        String current = ConsoleUtils.readString("Senha atual: ", null);
        String nova = ConsoleUtils.readString("Nova senha: ", null);
        authService.changePassword(SessionManager.getCurrentUserId(), current, nova);
        userMenuView.renderMessageLine("Senha alterada com sucesso!");
    }
    // #endregion User Profile Management
}
