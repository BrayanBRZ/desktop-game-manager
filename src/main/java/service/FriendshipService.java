// service/FriendshipService.java
package service;

import dao.UserDAO;
import dao.FriendRequestDAO;
import model.FriendRequest;
import model.FriendRequestState;
import model.User;

public class FriendshipService {
    private final UserDAO userDAO = new UserDAO();
    private final FriendRequestDAO requestDAO = new FriendRequestDAO();

    public User sendFriendRequest(Long fromId, String toName) throws ValidationException {
        User from = userDAO.findById(fromId);
        User to = userDAO.findByName(toName);
        if (from == null || to == null) throw new ValidationException("Usuário não encontrado");
        if (from.getId().equals(to.getId())) throw new ValidationException("Você não pode se adicionar");

        // Verificações
        if (from.getFriends().contains(to)) throw new ValidationException("Já são amigos");
        if (hasPendingRequestBetween(from, to)) throw new ValidationException("Já existe solicitação pendente");

        FriendRequest req = new FriendRequest(from, to, FriendRequestState.PENDING);
        from.getSentRequests().add(req);
        to.getPendingRequests().add(req);

        userDAO.update(from);
        userDAO.update(to);
        requestDAO.save(req);

        return refreshUser(fromId);
    }

    public void acceptRequest(Long requestId, Long userId) throws ValidationException {
        FriendRequest req = requestDAO.findById(requestId);
        if (req == null || !req.getToUser().getId().equals(userId))
            throw new ValidationException("Solicitação inválida");
        if (req.getStatus() != FriendRequestState.PENDING)
            throw new ValidationException("Solicitação já respondida");

        req.setStatus(FriendRequestState.ACCEPTED);
        requestDAO.update(req);

        User from = req.getFromUser();
        User to = req.getToUser();

        // Adiciona nas duas direções
        from.getFriends().add(to);
        to.getFriends().add(from);

        // Remove das listas pendentes/enviadas
        from.getSentRequests().remove(req);
        to.getPendingRequests().remove(req);

        userDAO.update(from);
        userDAO.update(to);
    }

    public void rejectRequest(Long requestId, Long userId) throws ValidationException {
        FriendRequest req = requestDAO.findById(requestId);
        if (req == null || !req.getToUser().getId().equals(userId))
            throw new ValidationException("Solicitação inválida");

        req.setStatus(FriendRequestState.REJECTED);
        requestDAO.update(req);

        User from = req.getFromUser();
        User to = req.getToUser();
        from.getSentRequests().remove(req);
        to.getPendingRequests().remove(req);

        userDAO.update(from);
        userDAO.update(to);
    }

    private boolean hasPendingRequestBetween(User a, User b) {
        return a.getSentRequests().stream().anyMatch(r -> r.getToUser().equals(b) && r.getStatus() == FriendRequestState.PENDING) ||
               b.getSentRequests().stream().anyMatch(r -> r.getToUser().equals(a) && r.getStatus() == FriendRequestState.PENDING);
    }

    private User refreshUser(Long id) {
        return userDAO.findById(id);
    }
}