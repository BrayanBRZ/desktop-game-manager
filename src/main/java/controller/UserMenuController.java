package controller;

import model.Game;
import model.User;
import model.UserGame;
import model.UserGameState;

import service.UserService;
import service.UserGameService;
import service.ServiceException;
import service.ValidationException;

import util.ConsoleUtils;
import view.GameView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

public class UserMenuController {

    private final UserService userService = new UserService();
    private final UserGameService userGameService = new UserGameService();
    private User loggedInUser = null;

    protected void loginOrRegister()
            throws ServiceException, ValidationException {
        System.out.println("\n--- LOGIN / REGISTRO ---");
        String name = ConsoleUtils.readString("Nome de usuário: ");
        User user = userService.findByName(name);

        if (user == null) {
            System.out.println("Usuário não encontrado. Deseja registrar? (s/n)");
            if (ConsoleUtils.readString("").equalsIgnoreCase("s")) {
                String password = ConsoleUtils.readString("Senha: ");
                loggedInUser = userService.registerUser(name, password);
                System.out.println("Usuário '" + loggedInUser.getName() + "' registrado e logado com sucesso!");
                runUserMenu();
            }
        } else {
            String password = ConsoleUtils.readString("Senha: ");
            loggedInUser = userService.login(name, password);
            if (loggedInUser == null) {
                System.out.println("Credenciais inválidas.");
            } else {
                System.out.println("Bem-vindo(a), " + loggedInUser.getName() + "!");
                runUserMenu();
            }
        }
    }

    private void runUserMenu() throws ServiceException, ValidationException {
        String option;
        do {
            System.out.println("\n--- MENU DO USUÁRIO: " + loggedInUser.getName() + " ---");
            System.out.println("1 - Ver minha biblioteca");
            System.out.println("2 - Adicionar jogo à biblioteca");
            System.out.println("3 - Remover jogo da biblioteca");
            System.out.println("4 - Atualizar estado do jogo");
            System.out.println("5 - Atualizar perfil");
            System.out.println("6 - Alterar senha");
            System.out.println("7 - Logout");
            System.out.print("Escolha uma opção: ");
            option = ConsoleUtils.readString("Escolha: ");

            switch (option) {
                case "1":
                    viewMyLibrary();
                    break;
                case "2":
                    addGameToLibrary();
                    break;
                case "3":
                    removeGameFromLibrary();
                    break;
                case "4":
                    updateGameState();
                    break;
                case "5":
                    updateUserProfile();
                    break;
                case "6":
                    changePassword();
                    break;
                case "0":
                    loggedInUser = null;
                    System.out.println("Logout realizado com sucesso.");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (!option.equals("0"));
    }

    // --- LÓGICA DE USUÁRIO ---
    private void viewMyLibrary() throws ServiceException {
        User user = userService.findById(loggedInUser.getId());
        Set<UserGame> library = user.getUserGames();
        System.out.println("\n--- Minha Biblioteca (" + library.size() + ") ---");
        if (library.isEmpty()) {
            System.out.println("Biblioteca vazia.");
        } else {
            library.forEach(ug -> {
                Game game = ug.getGame();
                System.out.printf(
                        "ID: %d | %s | Estado: %s | Tempo: %.1f h | Estimado: %s | Última vez: %s%n",
                        game.getId(),
                        game.getName(),
                        ug.getGameState(),
                        ug.getTotaltimePlayed(),
                        ug.isEstimated() ? "Sim" : "Não",
                        ConsoleUtils.formatDateTime(ug.getLastTimePlayed())
                );
            });
        }
    }

    private void addGameToLibrary() throws ServiceException, ValidationException {
        System.out.println("\n--- ADICIONAR JOGO À BIBLIOTECA ---");
        GameView.listarTodosJogos();
        Long gameId = ConsoleUtils.readLong("ID do jogo: ");
        userService.addGameToLibrary(loggedInUser.getId(), gameId);
        System.out.println("Jogo adicionado com sucesso!");
    }

    private void removeGameFromLibrary() throws ServiceException, ValidationException {
        System.out.println("\n--- REMOVER JOGO DA BIBLIOTECA ---");
        viewMyLibrary();
        Long gameId = ConsoleUtils.readLong("ID do jogo: ");
        userService.removeGameFromLibrary(loggedInUser.getId(), gameId);
        System.out.println("Jogo removido com sucesso!");
    }

    private void updateGameState() throws ServiceException, ValidationException {
        System.out.println("\n--- ATUALIZAR ESTADO DO JOGO ---");
        viewMyLibrary();
        Long gameId = ConsoleUtils.readLong("ID do jogo: ");
        UserGame userGame = userGameService.findByUserAndGame(loggedInUser.getId(), gameId);
        if (userGame == null) {
            System.out.println("Jogo não encontrado na biblioteca.");
            return;
        }

        System.out.println("Estado atual: " + userGame.getGameState());
        System.out.println("Estados disponíveis: " + Arrays.toString(UserGameState.values()));
        String stateStr = ConsoleUtils.readString("Novo estado: ");
        UserGameState state;
        try {
            state = UserGameState.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Estado inválido.");
            return;
        }

        String estimatedStr = ConsoleUtils.readString("Estimado? (s/n): ");
        boolean estimated = estimatedStr.equalsIgnoreCase("s");

        double totalTime = ConsoleUtils.readDouble("Tempo total jogado (horas): ");
        LocalDateTime lastPlayed = ConsoleUtils.readDataHora("Última vez jogado (dd/MM/yyyy HH:mm, ou Enter para agora): ",
                LocalDateTime.now());

        userGameService.updateAllAttributes(loggedInUser.getId(), gameId, estimated, state, totalTime, lastPlayed);
        System.out.println("Estado do jogo atualizado com sucesso!");
    }

    private void updateUserProfile() throws ServiceException, ValidationException {
        System.out.println("\n--- ATUALIZAR PERFIL ---");
        String name = ConsoleUtils.readString("Novo nome (Enter para manter '" + loggedInUser.getName() + "'): ");
        if (name.isEmpty()) {
            name = loggedInUser.getName();
        }

        LocalDate birthDate = ConsoleUtils.readData("Data de nascimento (dd/MM/yyyy, ou Enter para manter): ",
                loggedInUser.getBirthDate());
        String avatarPath = ConsoleUtils.readString("Caminho do avatar (Enter para manter): ");

        loggedInUser = userService.updateUserProfile(loggedInUser.getId(), name, birthDate,
                avatarPath.isEmpty() ? null : avatarPath);
        System.out.println("Perfil atualizado com sucesso!");
    }

    private void changePassword() throws ServiceException, ValidationException {
        System.out.println("\n--- ALTERAR SENHA ---");
        String currentPassword = ConsoleUtils.readString("Senha atual: ");
        String newPassword = ConsoleUtils.readString("Nova senha: ");
        userService.changePassword(loggedInUser.getId(), currentPassword, newPassword);
        System.out.println("Senha alterada com sucesso!");
    }
}
