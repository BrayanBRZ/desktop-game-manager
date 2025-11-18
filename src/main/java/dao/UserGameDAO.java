package dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.user.UserGame;
import model.user.UserGameState;

public class UserGameDAO extends GenericDAO<UserGame, Long> {

    // Constructor
    public UserGameDAO() {
        super();
    }

    /**
     * Finds a specific library entry for a given user and game. This is useful
     * for checking if a user already has a game in their library or for
     * retrieving specific data like playtime.
     *
     * @param userId The ID of the User.
     * @param gameId The ID of the Game.
     * @return The found UserGame entity, or {@code null} if it does not exist.
     */
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

    /**
     * Lista todos os jogos de um usuário.
     */
    public List<UserGame> findAllByUser(Long userId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT ug FROM UserGame ug WHERE ug.user.id = :userId";
            TypedQuery<UserGame> query = em.createQuery(jpql, UserGame.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        });
    }

    /**
     * Lista todos os jogos de um usuário que possuem flag 'estimated' = true.
     */
    public List<UserGame> findByEstimated(Long userId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT ug FROM UserGame ug WHERE ug.user.id = :userId AND ug.estimated = true";
            TypedQuery<UserGame> query = em.createQuery(jpql, UserGame.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        });
    }

    /**
     * Lista todos os jogos de um usuário com determinado estado (gameState).
     */
    public List<UserGame> findByGameState(Long userId, UserGameState state) {
        return executeInTransaction(em -> {
            String jpql = "SELECT ug FROM UserGame ug WHERE ug.user.id = :userId AND ug.gameState = :state";
            TypedQuery<UserGame> query = em.createQuery(jpql, UserGame.class);
            query.setParameter("userId", userId);
            query.setParameter("state", state);
            return query.getResultList();
        });
    }
}
