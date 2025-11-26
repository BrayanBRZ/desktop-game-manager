package controller.user;

import model.user.User;
import model.user.UserGame;
import model.user.UserGameState;

import service.AuthService;
import service.FriendshipService;
import service.UserGameService;
import service.UserService;

import service.exception.ServiceException;
import service.exception.ValidationException;

import session.SessionManager;

import util.ConsoleUtils;
import util.Injector;

import view.GameView;
import view.MenuRenderer;
import view.UserView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

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

    public void loginOrRegister() {
        ConsoleUtils.clearScreen();

        System.out.println("( LOGIN / REGISTRO )");
        String name = ConsoleUtils.readString("Nome de usuário: ").trim();

        User user;

        try {
            user = userService.findByName(name);
        } catch (ServiceException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
            ConsoleUtils.waitEnter();
            return;
        }

        if (user == null) { // Usuario nao existe
            String answer = ConsoleUtils
                    .readString("Usuário não encontrado. Deseja criar uma conta? (s/n): ")
                    .trim();

            if (!answer.equalsIgnoreCase("s")) {
                return;
            }

            String password = ConsoleUtils.readString("Nova senha: ");

            try {
                user = authService.register(name, password);
                System.out.println("Conta criada com sucesso!");
            } catch (ValidationException e) {
                System.out.println("Erro ao registrar: " + e.getMessage());
                ConsoleUtils.waitEnter();
                return;
            }
        }  else { // Usuario existe
            String password = ConsoleUtils.readString("Senha: ");

            try {
                user = authService.login(name, password);
            } catch (ValidationException e) {
                System.out.println("Credenciais inválidas.");
                ConsoleUtils.waitEnter();
                return;
            }
        }

        // Sucesso
        SessionManager.login(user);
        System.out.println("Bem-vindo(a), " + user.getName() + "!");
        ConsoleUtils.waitEnter();

        runMainMenu();
    }

    private void runMainMenu() {
        String option;
        while (true) {
            User currentUser = SessionManager.getCurrentUser();

            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner("Home -> User Menu - " + currentUser.getName());

            // int pending = friendshipService.getPendingReceivedCount(SessionManager.getCurrentUserId());
            String friendText = "7 - Amizades";
            // if (pending > 0) {
            //     friendText += " (" + pending + " pendente" + (pending > 1 ? "s" : "") + ")";
            // }

            MenuRenderer.renderOptions(
                    "1 - Minha biblioteca",
                    "2 - Adicionar jogo",
                    "3 - Remover jogo",
                    "4 - Atualizar progresso de jogo",
                    "5 - Atualizar perfil",
                    "6 - Alterar senha",
                    friendText,
                    "0 - Logout"
            );
            option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {

                    case "1":
                        showLibrary();
                        break;
                    case "2":
                        addGameToLibrary();
                        break;
                    case "3":
                        removeGameFromLibrary();
                        break;
                    case "4":
                        updateGameProgress();
                        break;
                    case "5":
                        updateProfile();
                        break;
                    case "6":
                        changePassword();
                        break;
                    case "7":
                        friendMenuController.friendshipMenu();
                        break;
                    case "0": {
                        SessionManager.logout();
                        System.out.println("Logout realizado com sucesso.");
                        break;
                    }
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (ValidationException e) {
                System.out.println("Erro de validação: " + e.getMessage());
            } catch (ServiceException e) {
                System.out.println("Erro no serviço: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
            }
        }
    }

    // #region User Library Management
    private void showLibrary() {
        User user = SessionManager.getCurrentUser();
        UserView.showLibrary(user.getUserGames());
    }

    private void addGameToLibrary() throws ServiceException, ValidationException {
        System.out.println("\n--- ADICIONAR JOGO ---");
        GameView.listAll();
        Long gameId = ConsoleUtils.readLong("ID do jogo: ");
        userService.addGameToLibrary(SessionManager.getCurrentUserId(), gameId);
        System.out.println("Jogo adicionado com sucesso!");
        ConsoleUtils.waitEnter();
    }

    private void removeGameFromLibrary() throws ServiceException, ValidationException {
        Long gameId = ConsoleUtils.readLong("ID do jogo para remover: ");
        userService.removeGameFromLibrary(SessionManager.getCurrentUserId(), gameId);
        System.out.println("Jogo removido com sucesso!");
        ConsoleUtils.waitEnter();
    }

    private void updateGameProgress() throws ServiceException, ValidationException {
        Long gameId = ConsoleUtils.readLong("ID do jogo: ");
        UserGame ug = userGameService.findByUserAndGame(SessionManager.getCurrentUserId(), gameId);
        if (ug == null) {
            System.out.println("Jogo não encontrado na sua biblioteca.");
            return;
        }

        System.out.println("Estado atual: " + ug.getGameState());
        System.out.println("Estados: " + Arrays.toString(UserGameState.values()));
        String stateInput = ConsoleUtils.readString("Novo estado: ").toUpperCase();

        UserGameState state;
        try {
            state = UserGameState.valueOf(stateInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Estado inválido.");
            return;
        }

        boolean estimated = ConsoleUtils.readString("Tempo estimado? (s/n): ").equalsIgnoreCase("s");
        double hours = ConsoleUtils.readDouble("Horas jogadas: ");
        LocalDateTime lastPlayed = ConsoleUtils.readDataHora("Última sessão (ou Enter para agora): ", LocalDateTime.now());

        userGameService.updateAllAttributes(SessionManager.getCurrentUserId(), gameId, estimated, state, hours, lastPlayed);
        System.out.println("Progresso atualizado!");
        ConsoleUtils.waitEnter();
    }
    // #endregion User Library Management

    // #region User Profile Management
    private void updateProfile() throws ServiceException, ValidationException {
        User user = SessionManager.getCurrentUser();
        String name = ConsoleUtils.readString("Novo nome (Enter para manter '" + user.getName() + "'): ");
        if (name.isEmpty()) {
            name = user.getName();
        }

        LocalDate birth = ConsoleUtils.readData("Nascimento (dd/MM/yyyy ou Enter): ", user.getBirthDate());

        userService.updateUserProfile(SessionManager.getCurrentUserId(), name, birth);
        System.out.println("Perfil atualizado!");
        // Atualiza sessao
        SessionManager.login(userService.findById(SessionManager.getCurrentUserId()));
        ConsoleUtils.waitEnter();
    }

    private void changePassword() throws ServiceException, ValidationException {
        String current = ConsoleUtils.readString("Senha atual: ");
        String nova = ConsoleUtils.readString("Nova senha: ");
        authService.changePassword(SessionManager.getCurrentUserId(), current, nova);
        System.out.println("Senha alterada com sucesso!");
        ConsoleUtils.waitEnter();
    }
    // #endregion User Profile Management
}
