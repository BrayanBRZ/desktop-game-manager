package service.user;

import model.user.FriendRequest;
import model.user.FriendRequestState;
import model.user.User;
import dao.user.FriendRequestDAO;
import dao.user.UserDAO;
import service.exception.ValidationException;
import java.util.Set;

public class FriendshipService {

    private final UserDAO userDAO = new UserDAO();
    private final FriendRequestDAO requestDAO = new FriendRequestDAO();

    public FriendRequest sendFriendRequest(Long fromUserId, Long toUserId) throws ValidationException {
        User from = userDAO.findById(fromUserId);
        User to = userDAO.findById(toUserId);

        if (from == null || to == null) throw new ValidationException("Usuário não encontrado");
        if (fromUserId.equals(toUserId)) throw new ValidationException("Você não pode se adicionar");

        if (requestDAO.findFriendsByUserId(fromUserId).stream()
                .anyMatch(u -> u.getId().equals(toUserId))) {
            throw new ValidationException("Já são amigos");
        }
        if (requestDAO.existsPendingBetween(fromUserId, toUserId)) {
            throw new ValidationException("Já existe solicitação pendente");
        }
        FriendRequest req = new FriendRequest(from, to, FriendRequestState.PENDING);
        
        requestDAO.save(req);
        from.getSentRequests().add(req);
        return req;
    }

    public void acceptRequest(Long requestId, Long userId) throws ValidationException {
        FriendRequest req = requestDAO.findById(requestId);
        if (req == null || !req.getToUser().getId().equals(userId)) {
            throw new ValidationException("Solicitação inválida");
        }
        if (req.getStatus() != FriendRequestState.PENDING) {
            throw new ValidationException("Solicitação já respondida");
        }

        req.setStatus(FriendRequestState.ACCEPTED);
        requestDAO.update(req);
    }

    public void acceptFriendRequestBetween(Long senderId, Long receiverId) throws ValidationException {
        FriendRequest req = getPendingReceived(receiverId).stream()
                .filter(r -> r.getFromUser().getId().equals(senderId))
                .findFirst()
                .orElseThrow(() -> new ValidationException("Solicitação não encontrada entre os usuários"));
        acceptRequest(req.getId(), receiverId);
    }

    public void rejectRequest(Long requestId, Long userId) throws ValidationException {
        FriendRequest req = requestDAO.findById(requestId);
        if (req == null || !req.getToUser().getId().equals(userId)) {
            throw new ValidationException("Solicitação inválida");
        }

        req.setStatus(FriendRequestState.REJECTED);
        requestDAO.update(req);
    }

    // #region Read-Only Operations
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
