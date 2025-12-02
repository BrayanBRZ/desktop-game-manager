package service.user;

import java.util.Set;

import dao.user.FriendRequestDAO;
import dao.user.UserDAO;
import model.user.FriendRequest;
import model.user.FriendRequestState;
import model.user.User;
import service.exception.ValidationException;

public class FriendshipService {

    private final UserDAO userDAO = new UserDAO();
    private final FriendRequestDAO requestDAO = new FriendRequestDAO();

    public void sendFriendRequest(Long fromUserId, Long toUserId) throws ValidationException {
        User from = userDAO.findById(fromUserId);
        User to = userDAO.findById(toUserId);

        if (from == null || to == null) throw new ValidationException("Usuário não encontrado");
        if (fromUserId.equals(toUserId)) throw new ValidationException("Você não pode se adicionar");

        // Verifica se já são amigos (via DAO)
        if (requestDAO.findFriendsByUserId(fromUserId).stream()
                .anyMatch(u -> u.getId().equals(toUserId))) {
            throw new ValidationException("Já são amigos");
        }

        // Verifica solicitação pendente nos dois sentidos (via DAO)
        if (requestDAO.existsPendingBetween(fromUserId, toUserId)) {
            throw new ValidationException("Já existe solicitação pendente");
        }

        FriendRequest req = new FriendRequest(from, to, FriendRequestState.PENDING);
        from.getSentRequests().add(req); // só o lado dono

        userDAO.update(from);
    }

    public void acceptRequest(Long requestId, Long userId) throws ValidationException {
        FriendRequest req = requestDAO.findById(requestId);
        if (req == null || !req.getToUser().getId().equals(userId))
            throw new ValidationException("Solicitação inválida");
        if (req.getStatus() != FriendRequestState.PENDING)
            throw new ValidationException("Solicitação já respondida");

        req.setStatus(FriendRequestState.ACCEPTED);
        requestDAO.update(req);
    }

    public void rejectRequest(Long requestId, Long userId) throws ValidationException {
        FriendRequest req = requestDAO.findById(requestId);
        if (req == null || !req.getToUser().getId().equals(userId))
            throw new ValidationException("Solicitação inválida");

        req.setStatus(FriendRequestState.REJECTED);
        requestDAO.update(req);
    }

    // #region Read-Only Operations
    public int getPendingReceivedCount(Long userId) {
        return requestDAO.findPendingReceivedByUserId(userId).size();
    }

    public Set<FriendRequest> getPendingReceived(Long userId) {
        return requestDAO.findPendingReceivedByUserId(userId);
    }

    public Set<FriendRequest> getSentPending(Long userId) {
        return requestDAO.findSentPendingByUserId(userId);
    }

    public Set<User> getFriends(Long userId) {
        return requestDAO.findFriendsByUserId(userId);
    }
    // #endregion Read-Only Operations
}