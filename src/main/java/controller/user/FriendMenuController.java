package controller.user;

import java.util.Set;

import model.user.FriendRequest;
import model.user.User;
import service.user.FriendshipService;
import service.user.UserService;
import service.session.SessionManager;
import service.exception.ServiceException;
import service.exception.ValidationException;
import view.user.UserConfigView;
import core.Navigation;
import utils.ConsoleUtils;

public class FriendMenuController {

    private final UserService userService;
    private final FriendshipService friendshipService;

    public FriendMenuController(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    private final UserConfigView userConfigView = new UserConfigView();

    public void friendshipMenu() {
        ConsoleUtils.clearScreen();
        Navigation.push("Friend Menu");

        while (true) {
            SessionManager.login(userService.findById(SessionManager.getCurrentUserId()));
            User currentUser = SessionManager.getCurrentUser();
            int pending = friendshipService.getPendingReceived(currentUser.getId()).size();
            int choice = userConfigView.renderBanner(
                    "Você tem " + pending + " solicitação(ões) pendente(s).",
                    "1 - Enviar solicitação de amizade",
                    "2 - Solicitações recebidas",
                    "3 - Solicitações enviadas",
                    "4 - Meus amigos",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        sendFriendRequest();
                        ConsoleUtils.waitEnter();
                        break;

                    case 2:
                        managePendingRequests();
                        ConsoleUtils.waitEnter();
                        break;

                    case 3:
                        viewSentPendingRequests();
                        ConsoleUtils.waitEnter();
                        break;

                    case 4:
                        userConfigView.showFriends(friendshipService.getFriends(currentUser.getId()));
                        ConsoleUtils.waitEnter();
                        break;

                    case 0:
                        Navigation.pop();
                        return;

                    default:
                        userConfigView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }

            } catch (ValidationException e) {
                userConfigView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                userConfigView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                userConfigView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    private void sendFriendRequest() throws ValidationException, ServiceException {
        Long toUserId = ConsoleUtils.readLong("ID do usuário destino: ", null);
        friendshipService.sendFriendRequest(SessionManager.getCurrentUserId(), toUserId);
        userConfigView.renderMessageLine("Solicitação enviada com sucesso!");
    }

    private void managePendingRequests() {
        Long userId = SessionManager.getCurrentUserId();
        Set<FriendRequest> pending = friendshipService.getPendingReceived(userId);

        userConfigView.showReceivedPendingRequests(pending);

        if (pending.isEmpty())  return;

        Long id = ConsoleUtils.readLong("ID da solicitação (0 para cancelar): ", null);
        if (id == 0) return;

        String action = userConfigView.readString("Aceitar (s) ou Rejeitar (n): ", null);
        boolean accept = action.equalsIgnoreCase("s");

        try {
            if (accept) {
                friendshipService.acceptRequest(id, userId);
                System.out.println("Amigo adicionado!");
            } else {
                friendshipService.rejectRequest(id, userId);
                System.out.println("Solicitação rejeitada.");
            }
        } catch (ValidationException | ServiceException e) {
            userConfigView.renderException(e);
        }
    }

    private void viewSentPendingRequests() {
        Long userId = SessionManager.getCurrentUserId();
        Set<FriendRequest> sent = friendshipService.getSentPending(userId);
        userConfigView.showSentPendingRequests(sent);
    }
}
