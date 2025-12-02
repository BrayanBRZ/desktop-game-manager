package view.user;

import model.user.User;
import model.user.UserGame;
import model.user.UserGameState;
import service.session.SessionManager;
import model.user.FriendRequest;
import utils.ConsoleUtils;
import view.BaseView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class UserMenuView extends BaseView {

    public class UserGameUpdateDTO {

        public final Long userId;
        public final Long gameId;

        public final boolean estimated;
        public final UserGameState state;
        public final double hours;
        public final LocalDateTime lastPlayed;

        public UserGameUpdateDTO(
                Long userId,
                Long gameId,
                boolean estimated,
                UserGameState state,
                double hours,
                LocalDateTime lastPlayed
        ) {
            this.userId = userId;
            this.gameId = gameId;
            this.estimated = estimated;
            this.state = state;
            this.hours = hours;
            this.lastPlayed = lastPlayed;
        }
    }

    public class UserProfileUpdateDTO {

        public final Long userId;
        public final String name;
        public final LocalDate birthDate;

        public UserProfileUpdateDTO(Long userId, String name, LocalDate birthDate) {
            this.userId = userId;
            this.name = name;
            this.birthDate = birthDate;
        }
    }

    public void showProfile(User user) {
        renderMessageLine("\n[ PERFIL DO USUÁRIO ]");

        System.out.printf("ID: %d%n", user.getId());
        System.out.printf("Nome: %s%n", user.getName());
        System.out.printf("Nascimento: %s%n",
                ConsoleUtils.formatDate(user.getBirthDate()));
        System.out.printf("Criado em: %s%n",
                ConsoleUtils.formatDateTime(user.getCreatedAt()));
        System.out.printf("Atualizado em: %s%n",
                ConsoleUtils.formatDateTime(user.getUpdatedAt()));
    }

    public UserGameUpdateDTO promptUpdateGameProgress(UserGame userGame) {

        renderMessageLine("Estado atual: " + userGame.getGameState());
        renderMessageLine("Estados disponíveis: " + Arrays.toString(UserGameState.values()));

        String stateInput = ConsoleUtils.readString("Novo estado: ").toUpperCase();
        UserGameState state;

        try {
            state = UserGameState.valueOf(stateInput);
        } catch (IllegalArgumentException e) {
            renderError("Estado inválido.");
            return null;
        }

        boolean estimated
                = ConsoleUtils.readString("O jogo é amado? (s/n): ").equalsIgnoreCase("s");

        double hours = ConsoleUtils.readDouble("Horas jogadas: ");

        LocalDateTime lastPlayed
                = ConsoleUtils.readDataHora("Última sessão (dd/MM/yyyy, ou Enter para agora): ",
                        LocalDateTime.now());

        return new UserGameUpdateDTO(
                SessionManager.getCurrentUserId(),
                userGame.getGame().getId(),
                estimated,
                state,
                hours,
                lastPlayed
        );
    }

    public UserProfileUpdateDTO promptProfileUpdate(User user) {

        String name = ConsoleUtils.readString(
                "Novo nome (Enter para manter '" + user.getName() + "'): "
        );

        if (name.isEmpty()) {
            name = user.getName();
        }

        LocalDate birth = ConsoleUtils.readData(
                "Nascimento (dd/MM/yyyy ou Enter): ",
                user.getBirthDate()
        );

        return new UserProfileUpdateDTO(
                user.getId(),
                name,
                birth
        );
    }

    public void displayUserList(List<User> users) {
        renderMessageLine("\n[ LISTA DE USUÁRIOS ]");
        if (users.isEmpty()) {
            renderMessageLine("Nenhum usuário cadastrado.");
        } else {
            users.forEach(u
                    -> System.out.printf("ID: %d - %s%n", u.getId(), u.getName()));
        }
    }

    public void showLibrary(Set<UserGame> library) {
        renderMessageLine("\n[ MINHA BIBLIOTECA (" + library.size() + ") ]");
        if (library.isEmpty()) {
            renderMessageLine("Você ainda não tem jogos na biblioteca.");
            return;
        }
        library.forEach(updateGame -> {
            System.out.printf(
                    "ID: %d | %s | %s | %.1fh | %s | Última jogada: %s%n",
                    updateGame.getGame().getId(),
                    updateGame.getGame().getName(),
                    updateGame.getGameState(),
                    updateGame.getTotaltimePlayed(),
                    updateGame.isEstimated() ? "Estimado" : "Padrão",
                    ConsoleUtils.formatDateTime(updateGame.getLastTimePlayed())
            );
        });
    }

    public void displayFriends(Set<User> friends) {
        renderMessageLine("\n[ MEUS AMIGOS ]");
        if (friends.isEmpty()) {
            renderMessageLine("Nenhum amigo encontrado.");
            return;
        }
        friends.forEach(u
                -> System.out.printf("ID: %d | Nome: %s%n", u.getId(), u.getName()));
    }

    public void showReceivedPendingRequests(Set<FriendRequest> requests) {
        renderMessageLine("\n[ SOLICITAÇÕES RECEBIDAS ]");
        if (requests.isEmpty()) {
            renderMessageLine("Nenhuma solicitação pendente.");
            return;
        }
        requests.forEach(r
                -> System.out.printf("ID %d | De: %s | %s%n",
                        r.getId(),
                        r.getFromUser().getName(),
                        ConsoleUtils.formatDateTime(r.getCreatedAt())));
    }

    public void showSentPendingRequests(Set<FriendRequest> requests) {
        renderMessageLine("\n[ SOLICITAÇÕES ENVIADAS ]");
        if (requests.isEmpty()) {
            renderMessageLine("Nenhuma solicitação pendente.");
            return;
        }
        requests.forEach(r
                -> System.out.printf("ID %d | Para: %s | %s%n",
                        r.getId(),
                        r.getToUser().getName(),
                        ConsoleUtils.formatDateTime(r.getCreatedAt())));
    }
}
