package view;

import java.util.Set;

import model.game.Game;
import model.user.FriendRequest;
import model.user.User;
import model.user.UserGame;
import util.ConsoleUtils;

public class UserView {

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
        ConsoleUtils.printUsersSet(friends, "MEUS AMIGOS");
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
}
