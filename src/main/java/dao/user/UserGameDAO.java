package dao.user;

import model.user.UserGame;
import model.user.UserGameState;
import dao.GenericDAO;
import utils.MyLinkedList;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class UserGameDAO extends GenericDAO<UserGame> {

    public UserGameDAO() {
        super(UserGame.class);
    }

    // #region Finders
    public UserGame findByUserAndGame(Long userId, Long gameId) {
        return executeInTransaction(em -> {
            try {
                String jpql = "SELECT ug FROM UserGame ug WHERE ug.user.id = :userId AND ug.game.id = :gameId";
                TypedQuery<UserGame> query = em.createQuery(jpql, UserGame.class);
                query.setParameter("userId", userId);
                query.setParameter("gameId", gameId);
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null; // return q.getResultStream().findFirst().orElse(null);
            }
        });
    }

    public MyLinkedList<UserGame> findAllByUser(Long userId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT ug FROM UserGame ug WHERE ug.user.id = :userId";
            TypedQuery<UserGame> query = em.createQuery(jpql, UserGame.class);
            query.setParameter("userId", userId);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    public MyLinkedList<UserGame> findByEstimated(Long userId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT ug FROM UserGame ug WHERE ug.user.id = :userId AND ug.estimated = true";
            TypedQuery<UserGame> query = em.createQuery(jpql, UserGame.class);
            query.setParameter("userId", userId);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    public MyLinkedList<UserGame> findByGameState(Long userId, UserGameState state) {
        return executeInTransaction(em -> {
            String jpql = "SELECT ug FROM UserGame ug WHERE ug.user.id = :userId AND ug.gameState = :state";
            TypedQuery<UserGame> query = em.createQuery(jpql, UserGame.class);
            query.setParameter("userId", userId);
            query.setParameter("state", state);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }
    // #endregion Finders
}
