package dao;

import model.user.FriendRequest;
import model.user.FriendRequestState;
import model.user.User;

import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;

public class FriendRequestDAO extends GenericDAO<FriendRequest> {

    public FriendRequestDAO() {
        super(FriendRequest.class);
    }

    public Set<User> findFriendsByUserId(Long userId) {
        return executeInTransaction(em -> {
            String jpql = 
                "SELECT DISTINCT u FROM User u "
                    + "WHERE EXISTS (SELECT 1 FROM FriendRequest fr "
                    + "WHERE fr.status = :status "
                    + "AND ((fr.fromUser.id = :userId AND fr.toUser.id = u.id) OR "
                    + "(fr.toUser.id = :userId AND fr.fromUser.id = u.id)))";

            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("userId", userId);
            query.setParameter("status", FriendRequestState.ACCEPTED);

            return new HashSet<>(query.getResultList());
        });
    }

    public Set<FriendRequest> findSentPendingByUserId(Long fromUserId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT fr FROM FriendRequest fr JOIN FETCH fr.toUser "
                + "WHERE fr.fromUser.id = :fromUserId "
                + "AND fr.status = :status "
                + "ORDER BY fr.createdAt DESC";

            TypedQuery<FriendRequest> query = em.createQuery(jpql, FriendRequest.class);
            query.setParameter("fromUserId", fromUserId);
            query.setParameter("status", FriendRequestState.PENDING);
            return new HashSet<>(query.getResultList());
        });
    }

    public Set<FriendRequest> findPendingReceivedByUserId(Long toUserId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT fr FROM FriendRequest fr JOIN FETCH fr.fromUser "
                    + "WHERE fr.toUser.id = :toUserId AND fr.status = :status ORDER BY fr.createdAt DESC";

            TypedQuery<FriendRequest> query = em.createQuery(jpql, FriendRequest.class);
            query.setParameter("toUserId", toUserId);
            query.setParameter("status", FriendRequestState.PENDING);

            return new HashSet<>(query.getResultList());
        });
    }

    public Set<FriendRequest> findReceivedByUserIdAndStatus(Long toUserId, FriendRequestState status) {
        return executeInTransaction(em -> {
            String jpql = "SELECT fr FROM FriendRequest fr JOIN FETCH fr.fromUser "
                    + "WHERE fr.toUser.id = :toUserId AND fr.status = :status ORDER BY fr.createdAt DESC";

            TypedQuery<FriendRequest> query = em.createQuery(jpql, FriendRequest.class);
            query.setParameter("toUserId", toUserId);
            query.setParameter("status", status);

            return new HashSet<>(query.getResultList());
        });
    }

    public boolean existsPendingBetween(Long userId1, Long userId2) {
        return executeInTransaction(em -> {
            String jpql = "SELECT COUNT(fr) FROM FriendRequest fr WHERE fr.status = 'PENDING' AND ("
                    + "(fr.fromUser.id = :id1 AND fr.toUser.id = :id2) OR "
                    + "(fr.fromUser.id = :id2 AND fr.toUser.id = :id1))";

            TypedQuery<Long> query = em.createQuery(jpql, Long.class);
            query.setParameter("id1", userId1);
            query.setParameter("id2", userId2);

            return query.getSingleResult() > 0;
        });
    }

    @Override
    public FriendRequest findById(Long id) {
        return executeInTransaction(em -> {
            String jpql = "SELECT fr FROM FriendRequest fr JOIN FETCH fr.fromUser JOIN FETCH fr.toUser WHERE fr.id = :id";
            TypedQuery<FriendRequest> query = em.createQuery(jpql, FriendRequest.class);
            query.setParameter("id", id);
            return query.getResultStream().findFirst().orElse(null);
        });
    }
}
