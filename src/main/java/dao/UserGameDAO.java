package dao;

import model.UserGame;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class UserGameDAO extends GenericDAO<UserGame, Long> {

    /**
     * Finds a specific library entry for a given user and game.
     * This is useful for checking if a user already has a game in their library
     * or for retrieving specific data like playtime.
     *
     * @param userId The ID of the User.
     * @param gameId The ID of the Game.
     * @return The found UserGame entity, or {@code null} if it does not exist.
     */
    public UserGame findByUserAndGame(Long userId, Long gameId) {
        EntityManager em = factory.createEntityManager();
        try {
            // JPQL query to select a UserGame entry based on both user and game IDs.
            String jpql = "SELECT ug FROM UserGame ug WHERE ug.user.id = :userId AND ug.game.id = :gameId";

            TypedQuery<UserGame> query = em.createQuery(jpql, UserGame.class);
            query.setParameter("userId", userId);
            query.setParameter("gameId", gameId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }
}