package dao;

import model.UserGame;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Data Access Object (DAO) for the {@link UserGame} entity.
 * This class manages the link between a User and a Game in their library.
 *
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-12
 */
public class UserGameDAO extends GenericDAO<UserGame, Long> {

    /**
     * Constructs a new UserGameDAO.
     */
    public UserGameDAO() {
        super();
    }

    // ----------------------------------------------------------------------------------
    // Custom query methods specific to the UserGame entity can be added below.
    // ----------------------------------------------------------------------------------

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