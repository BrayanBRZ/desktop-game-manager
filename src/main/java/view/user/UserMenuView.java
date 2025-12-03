package view.user;

import model.user.User;
import model.user.UserGame;
import model.user.UserGameState;
import model.user.FriendRequest;
import service.session.SessionManager;
import view.BaseView;
import utils.ConsoleUtils;
import utils.MyLinkedList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import dto.UserDTO;
import dto.UserGameDTO;

public class UserMenuView extends BaseView {

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

    public UserGameDTO promptUpdateGameProgress(UserGame userGame) throws Exception {

        renderMessageLine("Estado atual: " + userGame.getGameState());
        renderMessageLine("Estados disponíveis: " + Arrays.toString(UserGameState.values()));

        String stateInput = readString("Novo estado: ").toUpperCase();
        UserGameState state;

        try {
            state = UserGameState.valueOf(stateInput);
        } catch (IllegalArgumentException e) {
            throw new Exception("Estado inválido.");
        }

        boolean estimated = readString("O jogo é amado? (s/n): ").equalsIgnoreCase("s");

        double hours = readDouble("Horas jogadas: ");

        LocalDateTime lastPlayed = readDateTimeDefault(
                "Última sessão (dd/MM/yyyy, ou Enter para agora): ",
                LocalDateTime.now().toString()
        );

        return new UserGameDTO(
                SessionManager.getCurrentUserId(),
                userGame.getGame().getId(),
                estimated,
                state,
                hours,
                lastPlayed
        );
    }

    public UserDTO promptProfileUpdate(User user) {

        String name = ConsoleUtils.readString(
                "Novo nome (Enter para manter '" + user.getName() + "'): ",
                user.getName()
        );

        LocalDate birthdate = ConsoleUtils.readData(
                "Nascimento (dd/MM/yyyy ou Enter): ",
                user.getBirthDate().toString()
        );

        return new UserDTO(
                user.getId(),
                name,
                null,
                birthdate
        );
    }

    public void displayUserList(MyLinkedList<User> users) {
        renderMessageLine("[ LISTA DE USUÁRIOS ]");
        if (users.isEmpty()) {
            renderMessageLine("Nenhum usuário cadastrado.");
        } else {
            users.forEach(u
                    -> System.out.printf("ID: %d - %s%n", u.getId(), u.getName()));
        }
    }

    public void showLibrary(Set<UserGame> library) {
        renderMessageLine("[ MINHA BIBLIOTECA (" + library.size() + ") ]");
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
        renderMessageLine("[ MEUS AMIGOS ]");
        if (friends.isEmpty()) {
            renderMessageLine("Nenhum amigo encontrado.");
            return;
        }
        friends.forEach(u
                -> System.out.printf("ID: %d | Nome: %s%n", u.getId(), u.getName()));
    }

    public void showReceivedPendingRequests(Set<FriendRequest> requests) {
        renderMessageLine("[ SOLICITAÇÕES RECEBIDAS ]");
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
