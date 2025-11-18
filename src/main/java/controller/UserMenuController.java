package controller;

import model.user.FriendRequest;
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

import view.GameView;
import view.UserView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import dao.FriendRequestDAO;

public class UserMenuController {

    private final AuthService authService;
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final UserGameService userGameService;

    public UserMenuController(AuthService authService, UserService userService,
            FriendshipService friendshipService, UserGameService userGameService) {
        this.authService = authService;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.userGameService = userGameService;
    }

    public void start() {
        loginOrRegister();
    }

    private void loginOrRegister() {
        System.out.println("\n=== LOGIN / REGISTRO ===");
        String name = ConsoleUtils.readString("Nome de usuário: ").trim();

        User user = null;
        try {
            user = userService.findByName(name);
        } catch (ServiceException e) {
            System.out.println("Erro ao buscar usuário: " + e.getMessage());
        }

        if (user == null) {
            System.out.print("Usuário não encontrado. Deseja criar uma conta? (s/n): ");
            if (!ConsoleUtils.readString("").equalsIgnoreCase("s")) {
                return;
            }
            String password = ConsoleUtils.readString("Nova senha: ");
            try {
                user = authService.register(name, password);
                System.out.println("Conta criada com sucesso!");
            } catch (ValidationException e) {
                System.out.println("Erro ao registrar: " + e.getMessage());
                return;
            }
        } else {
            String password = ConsoleUtils.readString("Senha: ");
            user = authService.login(name, password);
            if (user == null) {
                System.out.println("Credenciais inválidas.");
                return;
            }
        }

        SessionManager.login(user);
        System.out.println("Bem-vindo(a), " + user.getName() + "!");
        runMainMenu();
    }

    private void runMainMenu() {
        String option;
        do {
            User currentUser = SessionManager.getCurrentUser();
            System.out.println("\n=== MENU - " + currentUser.getName() + " ===");
            System.out.println("1 - Minha biblioteca");
            System.out.println("2 - Adicionar jogo");
            System.out.println("3 - Remover jogo");
            System.out.println("4 - Atualizar progresso de jogo");
            System.out.println("5 - Atualizar perfil");
            System.out.println("6 - Alterar senha");

            // int pending = friendshipService.getPendingReceivedCount(SessionManager.getCurrentUserId());
            String friendText = "7 - Amizades";
            // if (pending > 0) {
            //     friendText += " (" + pending + " pendente" + (pending > 1 ? "s" : "") + ")";
            // }
            System.out.println(friendText);

            System.out.println("0 - Logout");

            option = ConsoleUtils.readString("Opção: ");

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
                        friendshipMenu();
                        break;
                    case "0": {
                        SessionManager.logout();
                        System.out.println("Logout realizado com sucesso.");
                        break;
                    }
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (!"0".equals(option));
    }

    // ======================= BIBLIOTECA =======================
    private void showLibrary() {
        User user = SessionManager.getCurrentUser();
        UserView.showLibrary(user.getUserGames());
    }

    private void addGameToLibrary() throws ServiceException, ValidationException {
        System.out.println("\n--- ADICIONAR JOGO ---");
        GameView.listarTodosJogos();
        Long gameId = ConsoleUtils.readLong("ID do jogo: ");
        userService.addGameToLibrary(SessionManager.getCurrentUserId(), gameId);
        System.out.println("Jogo adicionado com sucesso!");
    }

    private void removeGameFromLibrary() throws ServiceException, ValidationException {
        Long gameId = ConsoleUtils.readLong("ID do jogo para remover: ");
        userService.removeGameFromLibrary(SessionManager.getCurrentUserId(), gameId);
        System.out.println("Jogo removido com sucesso!");
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
    }

    // ======================= PERFIL =======================
    private void updateProfile() throws ServiceException, ValidationException {
        User user = SessionManager.getCurrentUser();
        String name = ConsoleUtils.readString("Novo nome (Enter para manter '" + user.getName() + "'): ");
        if (name.isEmpty()) {
            name = user.getName();
        }

        LocalDate birth = ConsoleUtils.readData("Nascimento (dd/MM/yyyy ou Enter): ", user.getBirthDate());
        String avatar = ConsoleUtils.readString("Avatar (caminho ou Enter): ");

        userService.updateUserProfile(SessionManager.getCurrentUserId(), name, birth, avatar.isEmpty() ? null : avatar);
        System.out.println("Perfil atualizado!");
        // Atualiza sessão
        SessionManager.login(userService.findById(SessionManager.getCurrentUserId()));
    }

    private void changePassword() throws ServiceException, ValidationException {
        String current = ConsoleUtils.readString("Senha atual: ");
        String nova = ConsoleUtils.readString("Nova senha: ");
        authService.changePassword(SessionManager.getCurrentUserId(), current, nova);
        System.out.println("Senha alterada com sucesso!");
    }

    // ======================= AMIZADES =======================
    private void friendshipMenu() {
        String op;
        Long currentUserId = SessionManager.getCurrentUserId();
        FriendRequestDAO dao = new FriendRequestDAO();

        do {
            int pending = dao.findPendingReceivedByUserId(currentUserId).size();

            System.out.println("\n=== AMIZADES ===");
            System.out.println("Você tem " + pending + " solicitação(ões) pendente(s).");
            System.out.println("1 - Enviar solicitação");
            System.out.println("2 - Ver pendentes");
            System.out.println("3 - Minhas solicitações enviadas");
            System.out.println("4 - Meus amigos");
            System.out.println("0 - Voltar");

            op = ConsoleUtils.readString("Opção: ");

            try {
                switch (op) {
                    case "1":
                        sendFriendRequest();
                        break;
                    case "2":
                        managePendingRequests();
                        break;
                    case "3":
                        viewSentPendingRequests();
                        break;
                    case "4":
                        Set<User> friends = dao.findFriendsByUserId(currentUserId);
                        ConsoleUtils.printUsersSet(friends, "MEUS AMIGOS");
                        break;
                    case "0":
                        System.out.println("Voltando...");
                        break;
                    default:
                        System.out.println("Inválido.");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (!"0".equals(op));
    }

    private void sendFriendRequest() throws ValidationException {
        Long toUserId = ConsoleUtils.readLong("ID do usuário destino: ");
        friendshipService.sendFriendRequest(SessionManager.getCurrentUserId(), toUserId);
        System.out.println("Solicitação enviada com sucesso!");

        // Refresh da sessão para atualizar sentRequests
        SessionManager.login(userService.findById(SessionManager.getCurrentUserId()));
    }

    private void managePendingRequests() {
        Long userId = SessionManager.getCurrentUserId();
        Set<FriendRequest> pending = new FriendRequestDAO().findPendingReceivedByUserId(userId);

        UserView.showReceivedPendingRequests(pending);
        if (pending.isEmpty()) {
            return;
        }

        Long id = ConsoleUtils.readLong("ID da solicitação (0 para cancelar): ");
        if (id == 0) {
            return;
        }

        String acao = ConsoleUtils.readString("Aceitar (a) ou Rejeitar (r): ");
        boolean aceitar = acao.equalsIgnoreCase("a");

        try {
            if (aceitar) {
                friendshipService.acceptRequest(id, userId);
                System.out.println("Amigo adicionado!");
            } else {
                friendshipService.rejectRequest(id, userId);
                System.out.println("Solicitação rejeitada.");
            }
            // Refresh completo após aceitar/rejeitar
            SessionManager.login(userService.findById(userId));
        } catch (ValidationException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void viewSentPendingRequests() {
        Long userId = SessionManager.getCurrentUserId();
        Set<FriendRequest> sent = new FriendRequestDAO().findSentPendingByUserId(userId);
        UserView.showSentPendingRequests(sent);
    }
}
