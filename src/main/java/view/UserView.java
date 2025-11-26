package view;

import java.util.List;
import java.util.Set;

import model.game.Game;
import model.user.FriendRequest;
import model.user.User;
import model.user.UserGame;
import service.exception.ServiceException;
import service.user.UserService;
import util.ConsoleUtils;

public class UserView {

    private static final UserService userService = new UserService();

    public static void showLibrary(Set<UserGame> library) {
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

    public static void showFriends(Set<User> friends) {
        printUsersSet(friends, "MEUS AMIGOS");
    }

    public static void printUsersSet(Set<User> friends, String string) {
        System.out.println(string);
        if (friends.isEmpty()) {
            System.out.println("Nenhum usuário encontrado.");
        } else {
            friends.forEach(u -> System.out.printf("ID: %d | Nome: %s%n", u.getId(), u.getName()));
        }
    }

    public static void showReceivedPendingRequests(Set<FriendRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("Nenhuma solicitação recebida pensente.");
            return;
        }
        System.out.println("\n--- SOLICITAÇÕES RECEBIDAS PENDENTES ---");
        requests.forEach(r -> System.out.printf("ID %d | De: %s | %s%n",
                r.getId(), r.getFromUser().getName(),
                ConsoleUtils.formatDateTime(r.getCreatedAt())));
    }

    public static void showSentPendingRequests(Set<FriendRequest> requests) {
        if (requests.isEmpty()) {
            System.out.println("Nenhuma solicitação enviada pendente");
            return;
        }
        System.out.println("\n--- SOLICITAÇÕES ENVIADAS PENDENTES ---");
        requests.forEach(r -> System.out.printf("ID %d | Para: %s | %s%n",
                r.getId(), r.getToUser().getName(),
                ConsoleUtils.formatDateTime(r.getCreatedAt())));
    }

    public static void listAll() throws ServiceException {
        List<User> users = userService.findAll();
        System.out.println("\n[ LISTA DE USUÁRIOS ]");
        if (users.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
        } else {
            users.forEach(d -> System.out.println("ID: " + d.getId() + " - " + d.getName()));
        }
    }
}
