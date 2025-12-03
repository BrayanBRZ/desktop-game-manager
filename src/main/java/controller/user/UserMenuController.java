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
import dto.UserGameDTO;
import core.Injector;
import core.Navigation;
import dto.UserDTO;
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
        String name = userMenuView.readString("Nome de usuário: ").trim();

        User user = userService.findByName(name);

        if (user == null) { // Usuario nao existe
            if (!userMenuView.readString(
                    "Usuário não encontrado. Deseja criar uma conta? (s/n): ")
                    .trim().equalsIgnoreCase("s")) {
                return;
            }

            String password = userMenuView.readString("Nova senha: ");

            try {
                user = authService.register(name, password);
                userMenuView.renderMessageLine("Conta criada com sucesso!");
            } catch (ValidationException e) {
                userMenuView.renderMessageLine("Erro ao registrar: " + e.getMessage());

                return;
            }
        } else { // Usuario existe
            String password = userMenuView.readString("Senha: ");

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
        userMenuView.renderMessageLine("[ ADICIONAR JOGO ]");
        String gameName = userMenuView.readString("Nome do jogo: ");
        userService.addGameToLibrary(SessionManager.getCurrentUserId(), gameName);
        userMenuView.renderMessageLine("Jogo adicionado com sucesso!");
    }

    private void removeGameFromLibrary() throws ServiceException, ValidationException {
        userMenuView.renderMessageLine("[ REMOVER JOGO ]");
        Long gameId = userMenuView.readLong("ID do jogo para remover: ");
        userService.removeGameFromLibrary(SessionManager.getCurrentUserId(), gameId);
        userMenuView.renderMessageLine("Jogo removido com sucesso!");
    }

    public void updateGameProgress() throws Exception {
        Long gameId = userMenuView.readLong("ID do jogo: ");

        UserGame userGame = userGameService.findByUserAndGame(
                SessionManager.getCurrentUserId(), gameId
        );

        UserGameDTO dto = userMenuView.promptUpdateGameProgress(userGame);

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
        UserDTO dto = userMenuView.promptProfileUpdate(user);
        userService.updateUserProfile(dto.getId(), dto.getName(), dto.getBirthDate());
        userMenuView.renderMessageLine("Perfil atualizado!");
    }

    private void changePassword() throws ServiceException, ValidationException {
        String current = userMenuView.readString("Senha atual: ");
        String nova = userMenuView.readString("Nova senha: ");
        authService.changePassword(SessionManager.getCurrentUserId(), current, nova);
        userMenuView.renderMessageLine("Senha alterada com sucesso!");
    }
    // #endregion User Profile Management
}
