package view.user;

import model.user.FriendRequest;
import model.user.User;
import view.BaseView;
import utils.ConsoleUtils;
import utils.MyLinkedList;

import java.time.LocalDate;
import java.util.Set;

import dto.UserDTO;

public class UserConfigView extends BaseView {

    public void showFriends(Set<User> friends) {
        printUsersSet(friends, "MEUS AMIGOS");
    }

    public void printUsersSet(Set<User> friends, String string) {
        renderMessageLine(string);
        if (friends.isEmpty()) {
            renderMessageLine("Nenhum usuário encontrado.");
        } else {
            friends.forEach(u -> System.out.printf("ID: %d | Nome: %s%n", u.getId(), u.getName()));
        }
    }

    public void showReceivedPendingRequests(Set<FriendRequest> requests) {
        if (requests.isEmpty()) {
            renderMessageLine("Nenhuma solicitação recebida pendente.");
            return;
        }
        renderMessageLine("\n--- SOLICITAÇÕES RECEBIDAS PENDENTES ---");
        requests.forEach(r -> System.out.printf("ID %d | De: %s | %s%n",
                r.getId(), r.getFromUser().getName(),
                ConsoleUtils.formatDateTime(r.getCreatedAt())));
    }

    public void showSentPendingRequests(Set<FriendRequest> requests) {
        if (requests.isEmpty()) {
            renderMessageLine("Nenhuma solicitação enviada pendente");
            return;
        }
        renderMessageLine("[ SOLICITAÇÕES ENVIADAS PENDENTES ]");
        requests.forEach(r -> System.out.printf("ID %d | Para: %s | %s%n",
                r.getId(), r.getToUser().getName(),
                ConsoleUtils.formatDateTime(r.getCreatedAt())));
    }

    public void listAllUsers(MyLinkedList<User> users) {
        renderMessageLine("[ LISTA DE USUÁRIOS ]");
        if (users.isEmpty()) {
            renderMessageLine("Nenhum usuário cadastrado.");
        } else {
            users.forEach(d -> renderMessageLine("ID: " + d.getId() + " - " + d.getName()));
        }
    }

    public UserDTO promptUserCreate() {

        String name = readString("Novo: ");
        String password = readString("Nova senha: ");

        return new UserDTO(
                null, name, password, null
        );
    }

    public UserDTO promptUserUpdate(User user) {

        renderMessageLine("Editando usuário: " + user.getName());

        String newName = readStringDefault("Novo nome (Enter para manter '" + user.getName() + "'): ", user.getName());

        LocalDate newBirthDate = readDateDefault(
                "Nova data de nascimento (Enter para manter atual): ",
                user.getBirthDate());

        return new UserDTO(
                user.getId(), newName, null, newBirthDate
        );
    }
}
