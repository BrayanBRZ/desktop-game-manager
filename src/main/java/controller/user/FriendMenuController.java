package controller.user;

import java.util.Set;

import core.Navigation;
import dao.user.FriendRequestDAO;
import model.user.FriendRequest;
import model.user.User;
import service.exception.ServiceException;
import service.exception.ValidationException;
import service.session.SessionManager;
import service.user.FriendshipService;
import service.user.UserService;
import utils.ConsoleUtils;
import utils.MenuRenderer;
import view.user.UserConfigView;

public class FriendMenuController {

    private final UserService userService;
    private final FriendshipService friendshipService;
    private final FriendRequestDAO friendRequestDAO = new FriendRequestDAO();

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
            int pending = friendRequestDAO.findPendingReceivedByUserId(currentUser.getId()).size();
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
                        userConfigView.showFriends(friendRequestDAO.findFriendsByUserId(currentUser.getId()));
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
        Long toUserId = ConsoleUtils.readLong("ID do usuário destino: ");
        friendshipService.sendFriendRequest(SessionManager.getCurrentUserId(), toUserId);
        userConfigView.renderMessageLine("Solicitação enviada com sucesso!");
    }

    private void managePendingRequests() {
        Long userId = SessionManager.getCurrentUserId();
        Set<FriendRequest> pending = friendRequestDAO.findPendingReceivedByUserId(userId);

        userConfigView.showReceivedPendingRequests(pending);

        if (pending.isEmpty())  return;

        Long id = ConsoleUtils.readLong("ID da solicitação (0 para cancelar): ");
        if (id == 0) return;

        String action = ConsoleUtils.readString("Aceitar (s) ou Rejeitar (n): ");
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
            MenuRenderer.renderException(e);
        }
    }

    private void viewSentPendingRequests() {
        Long userId = SessionManager.getCurrentUserId();
        Set<FriendRequest> sent = friendRequestDAO.findSentPendingByUserId(userId);
        userConfigView.showSentPendingRequests(sent);
    }
}
