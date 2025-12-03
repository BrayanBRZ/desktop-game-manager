package core.seed;

import com.github.javafaker.Faker;

import model.game.Game;
import model.user.User;
import model.user.UserGame;
import model.user.UserGameState;
import service.session.AuthService;
import service.user.FriendshipService;
import service.user.UserGameService;
import service.user.UserService;
import utils.MyLinkedList;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Random;

import service.game.GameService;
import view.SeederConfigView;

public class UserSeeder {

    private final AuthService authService;
    private final UserService userService;
    private final UserGameService userGameService;
    private final FriendshipService friendshipService;
    private final GameService gameService;

    // Utilities
    private final Faker faker;
    private final Random random;

    // Seeder Configs
    private static final int MAX_GAMES_USER = 5;
    private static final int MAX_FRIENDS_USER = 3;

    public UserSeeder(AuthService authService, UserService userService,
            UserGameService userGameService, FriendshipService friendshipService, GameService gameService) {
        this.authService = authService;
        this.userService = userService;
        this.userGameService = userGameService;
        this.friendshipService = friendshipService;
        this.gameService = gameService;

        this.faker = new Faker(new Locale("pt-BR"));
        this.random = new Random();
    }

    SeederConfigView seederConfigView = new SeederConfigView();

    public void seed(int quantity) throws Exception {
        try {
            seederConfigView.renderMessage("[ Iniciando Seed de Usuários ]");
            MyLinkedList<User> createdUsers = seedUsers(quantity);

            seederConfigView.renderMessage("[ Povoando Bibliotecas ]");
            seedLibraries(createdUsers);

            seederConfigView.renderMessage("[ Criando Amizades ]");
            seedFriendships(createdUsers);

        } catch (Exception e) {
            throw new Exception("Erro crítico no UserSeeder: " + e.getMessage());
        }
    }

    private MyLinkedList<User> seedUsers(int quantity) {
        MyLinkedList<User> users = new MyLinkedList<>();

        for (int i = 0; i < quantity; i++) {
            try {
                String username = faker.name().username().replace(".", "") + random.nextInt(1000);
                String password = "123456"; // Senha padrão

                User user = authService.register(username, password);

                LocalDate birthDate = faker.date().birthday(18, 50)
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                userService.updateUserProfile(user.getId(), user.getName(), birthDate);

                users.add(user);
                seederConfigView.renderMessage("Usuário criado: " + username);

            } catch (Exception e) {
                seederConfigView.renderError("Ignorando erro ao criar usuário (provável duplicata): " + e.getMessage());
            }
        }
        return users;
    }

    private void seedLibraries(MyLinkedList<User> users) {

        MyLinkedList<Game> allGames = gameService.findAll();

        if (allGames.isEmpty()) {
            seederConfigView.renderMessage("Nenhum jogo encontrado para adicionar às bibliotecas.");
            return;
        }

        for (User user : users) {
            int qtGames = random.nextInt(MAX_GAMES_USER + 1);

            for (int i = 0; i < qtGames; i++) {
                try {
                    Game randomGame = allGames.get(random.nextInt(allGames.size()));

                    userService.addGameToLibrary(user.getId(), randomGame.getName());

                    UserGame ug = userGameService.findByUserAndGame(user.getId(), randomGame.getId());

                    if (ug != null) {
                        UserGameState randomState = UserGameState.values()[random.nextInt(UserGameState.values().length)];
                        double randomHours = 1.0 + (100.0 - 1.0) * random.nextDouble(); // 1 a 100 horas
                        boolean isFavorite = random.nextBoolean();

                        userGameService.updateAllAttributes(
                                user.getId(),
                                randomGame.getId(),
                                isFavorite,
                                randomState,
                                Math.round(randomHours * 10.0) / 10.0,
                                java.time.LocalDateTime.now().minusDays(random.nextInt(30)) // Jogado nos últimos 30 dias
                        );
                    }

                } catch (Exception e) {
                    seederConfigView.renderError("Ignorando erro ao adicionar jogo ao usuário (provável duplicata): " + e.getMessage());
                }
            }
        }
    }

    private void seedFriendships(MyLinkedList<User> users) {
        if (users.size() < 2) {
            return;
        }

        for (User sender : users) {
            int qtdAmigos = random.nextInt(MAX_FRIENDS_USER + 1);

            for (int i = 0; i < qtdAmigos; i++) {
                try {
                    User receiver = users.get(random.nextInt(users.size()));

                    if (sender.getId().equals(receiver.getId())) {
                        continue;
                    }
                    model.user.FriendRequest request = friendshipService.sendFriendRequest(sender.getId(), receiver.getId());
                    friendshipService.acceptRequest(request.getId(), receiver.getId());
                    seederConfigView.renderMessage(sender.getName() + " virou amigo de " + receiver.getName());

                } catch (Exception e) {
                    seederConfigView.renderError("Ignorando erro ao adicionar amigo ao usuário (provável duplicata): " + e.getMessage());
                }
            }
        }
    }
}
