package controller;

import model.*;
import service.*;
import session.SessionManager;
import util.ConsoleUtils;
import view.GameView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class UserMenuController {

    // Services injetados (poderia ser via DI no futuro)
    private final AuthService authService = new AuthService();
    private final UserService userService = new UserService();
    private final FriendshipService friendshipService = new FriendshipService();
    private final RecommendationService recommendationService = new RecommendationService();
    private final UserGameService userGameService = new UserGameService();

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
            System.out.println("\n=== MENU • " + currentUser.getName() + " ===");
            System.out.println("1 - Minha biblioteca");
            System.out.println("2 - Adicionar jogo");
            System.out.println("3 - Remover jogo");
            System.out.println("4 - Atualizar progresso de jogo");
            System.out.println("5 - Atualizar perfil");
            System.out.println("6 - Alterar senha");

            int pending = currentUser.getPendingRequests().size();
            String friendText = "7 - Amizades";
            if (pending > 0) {
                friendText += " (" + pending + " pendente" + (pending > 1 ? "s" : "") + ")";
            }
            System.out.println(friendText);

            System.out.println("8 - Recomendações de amigos");
            System.out.println("0 - Logout");

            option = ConsoleUtils.readString("Opção: ");

            try {
                switch (option) {

                    case "1":
                        viewLibrary();
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
                    case "8":
                        showRecommendations();
                        break;
                    case "0": {
                        SessionManager.logout();
                        System.out.println("Logout realizado com sucesso.");
                        break;
                    }

                    default:
                        System.out.println("Opção inválida.");
                }
                break;
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (!"0".equals(option));
    }

    // ======================= BIBLIOTECA =======================
    private void viewLibrary() {
        User user = SessionManager.getCurrentUser();
        Set<UserGame> library = user.getUserGames();

        System.out.println("\n--- MINHA BIBLIOTECA (" + library.size() + ") ---");
        if (library.isEmpty()) {
            System.out.println("Você ainda não tem jogos na biblioteca.");
            return;
        }

        library.forEach(ug -> {
            Game g = ug.getGame();
            System.out.printf("ID: %d | %s | %s | %.1fh | %s | Última: %s%n",
                    g.getId(), g.getName(), ug.getGameState(),
                    ug.getTotaltimePlayed(), ug.isEstimated() ? "Estimado" : "Real",
                    ConsoleUtils.formatDateTime(ug.getLastTimePlayed()));
        });
    }

    private void addGameToLibrary() throws ServiceException, ValidationException {
        System.out.println("\n--- ADICIONAR JOGO ---");
        GameView.listarTodosJogos();
        Long gameId = ConsoleUtils.readLong("ID do jogo: ");
        userService.addGameToLibrary(SessionManager.getCurrentUserId(), gameId);
        System.out.println("Jogo adicionado com sucesso!");
    }

    private void removeGameFromLibrary() throws ServiceException, ValidationException {
        viewLibrary();
        Long gameId = ConsoleUtils.readLong("ID do jogo para remover: ");
        userService.removeGameFromLibrary(SessionManager.getCurrentUserId(), gameId);
        System.out.println("Jogo removido com sucesso!");
    }

    private void updateGameProgress() throws ServiceException, ValidationException {
        viewLibrary();
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
        do {
            User user = SessionManager.getCurrentUser();
            int pending = user.getPendingRequests().size();
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
                        viewSentRequests();
                        break;
                    case "4":
                        ConsoleUtils.printUsersSet(user.getFriends(), "MEUS AMIGOS");
                        break;
                    case "0":
                        System.out.println("Voltando...");
                        break;
                    default:
                        System.out.println("Inválido.");
                }
                break;
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        } while (!"0".equals(op));
    }

    private void sendFriendRequest() throws ValidationException {
        String name = ConsoleUtils.readString("Nome do usuário: ");
        friendshipService.sendFriendRequest(SessionManager.getCurrentUserId(), name);
        System.out.println("Solicitação enviada para " + name + "!");
        SessionManager.login(userService.findById(SessionManager.getCurrentUserId())); // refresh
    }

    private void managePendingRequests() {
        User user = SessionManager.getCurrentUser();
        Set<FriendRequest> pending = user.getPendingRequests();
        if (pending.isEmpty()) {
            System.out.println("Nenhuma solicitação pendente.");
            return;
        }

        pending.forEach(r
                -> System.out.printf("ID %d | De: %s | %s%n",
                        r.getId(),
                        r.getFromUser().getName(),
                        ConsoleUtils.formatDateTime(r.getCreatedAt())
                ));

        Long id = ConsoleUtils.readLong("ID da solicitação (0 cancelar): ");
        if (id == 0) {
            return;
        }

        String acao = ConsoleUtils.readString("Aceitar (a) ou Rejeitar (r): ");
        boolean aceitar = acao.equalsIgnoreCase("a");

        try {
            if (aceitar) {
                friendshipService.acceptRequest(id, SessionManager.getCurrentUserId());
                System.out.println("Amigo adicionado!");
            } else {
                friendshipService.rejectRequest(id, SessionManager.getCurrentUserId());
                System.out.println("Solicitação rejeitada.");
            }
            SessionManager.login(userService.findById(SessionManager.getCurrentUserId()));
        } catch (ValidationException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void viewSentRequests() {
        User user = SessionManager.getCurrentUser();
        user.getSentRequests().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .forEach(r -> System.out.printf("→ %s | %s%n",
                r.getToUser().getName(),
                r.getStatus()
        ));
    }

    // ======================= RECOMENDAÇÕES =======================
    private void showRecommendations() {
        System.out.println("\n--- RECOMENDAÇÕES DE AMIGOS ---");
        try {
            List<Game> recs = recommendationService.getRecommendations(
                    SessionManager.getCurrentUserId(), 6);

            if (recs.isEmpty()) {
                System.out.println("Sem recomendações. Adicione amigos ou explore mais jogos!");
            } else {
                System.out.println("Jogos que seus amigos estão jogando:");
                for (int i = 0; i < recs.size(); i++) {
                    Game g = recs.get(i);
                    System.out.printf("%d. %s%n", i + 1, g.getName());
                }
            }
        } catch (ServiceException e) {
            System.out.println("Erro ao carregar recomendações: " + e.getMessage());
        }
    }
}
