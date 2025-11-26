package controller.user;

import java.util.Set;

import dao.FriendRequestDAO;
import model.user.FriendRequest;
import model.user.User;
import service.FriendshipService;
import service.UserService;
import service.exception.ServiceException;
import service.exception.ValidationException;
import session.SessionManager;
import util.ConsoleUtils;
import util.Navigation;
import view.MenuRenderer;
import view.UserView;

public class FriendMenuController {

    private final UserService userService;
    private final FriendshipService friendshipService;
    private final FriendRequestDAO friendRequestDAO = new FriendRequestDAO();

    public FriendMenuController(UserService userService, FriendshipService friendshipService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
    }

    public void friendshipMenu() {
        Navigation.push("Friend Menu");

        while (true) {
            try {
                User currentUser = SessionManager.getCurrentUser();
                int pending = friendRequestDAO.findPendingReceivedByUserId(currentUser.getId()).size();

                ConsoleUtils.clearScreen();
                MenuRenderer.renderBanner(Navigation.getPath());

                MenuRenderer.renderOptions(
                        "Você tem " + pending + " solicitação(ões) pendente(s).",
                        "1 - Enviar solicitação de amizade",
                        "2 - Solicitações recebidas",
                        "3 - Solicitações enviadas",
                        "4 - Meus amigos",
                        "0 - Voltar"
                );

                String option = ConsoleUtils.readString("Opção: ");

                switch (option) {
                    case "1":
                        sendFriendRequest();
                        ConsoleUtils.waitEnter();
                        break;

                    case "2":
                        managePendingRequests();
                        ConsoleUtils.waitEnter();
                        break;

                    case "3":
                        viewSentPendingRequests();
                        ConsoleUtils.waitEnter();
                        break;

                    case "4":
                        UserView.showFriends(friendRequestDAO.findFriendsByUserId(currentUser.getId()));
                        ConsoleUtils.waitEnter();
                        break;

                    case "0":
                        Navigation.pop();
                        return;

                    default:
                        MenuRenderer.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }

            } catch (ValidationException e) {
                MenuRenderer.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                MenuRenderer.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                MenuRenderer.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    private void sendFriendRequest() throws ValidationException, ServiceException {
        Long toUserId = ConsoleUtils.readLong("ID do usuário destino: ");

        friendshipService.sendFriendRequest(SessionManager.getCurrentUserId(), toUserId);
        System.out.println("Solicitação enviada com sucesso!");

        // Atualiza usuário na sessão
        SessionManager.login(userService.findById(SessionManager.getCurrentUserId()));
    }

    private void managePendingRequests() {
        Long userId = SessionManager.getCurrentUserId();
        Set<FriendRequest> pending = friendRequestDAO.findPendingReceivedByUserId(userId);

        UserView.showReceivedPendingRequests(pending);

        if (pending.isEmpty()) {
            ConsoleUtils.waitEnter();
            return;
        }

        Long id = ConsoleUtils.readLong("ID da solicitação (0 para cancelar): ");
        if (id == 0) return;

        String action = ConsoleUtils.readString("Aceitar (a) ou Rejeitar (r): ");
        boolean accept = action.equalsIgnoreCase("a");

        try {
            if (accept) {
                friendshipService.acceptRequest(id, userId);
                System.out.println("Amigo adicionado!");
            } else {
                friendshipService.rejectRequest(id, userId);
                System.out.println("Solicitação rejeitada.");
            }

            // Atualiza sessão após ação
            SessionManager.login(userService.findById(userId));

        } catch (ValidationException | ServiceException e) {
            MenuRenderer.renderException(e);
        }
    }

    private void viewSentPendingRequests() {
        Long userId = SessionManager.getCurrentUserId();
        Set<FriendRequest> sent = friendRequestDAO.findSentPendingByUserId(userId);
        UserView.showSentPendingRequests(sent);
    }
}
