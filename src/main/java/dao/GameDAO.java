package dao;

import model.Game;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Data Access Object (DAO) for the {@link Game} entity.
 * This class extends the generic DAO implementation and provides
 * access to the common CRUD operations for Game entities.
 * It can also be used to implement custom, game-specific query methods.
 *
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-02
 */
public class GameDAO extends GenericDAO<Game, Long> {

    /**
     * Constructs a new GameDAO with the given EntityManager.
     *
     * @param entityManager The EntityManager to be used for database operations.
     */
    public GameDAO(EntityManager entityManager) {
        super(entityManager);
    }

    // ----------------------------------------------------------------------------------
    // Custom query methods specific to the Game entity can be added below.
    // ----------------------------------------------------------------------------------

    /**
     * Finds all games belonging to a specific genre.
     *
     * @param genreName The name of the genre to search for.
     * @return A list of games that match the specified genre.
     */
    public List<Game> findByGenre(String genreName) {
        // JPQL query to select games where the genre's name matches the parameter.
        String jpql = "SELECT g FROM Game g WHERE g.genre.name = :genreName";

        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("genreName", genreName);

        return query.getResultList();
    }

    /**
     * Finds all games available on a specific platform.
     *
     * @param platformName The name of the platform to search for.
     * @return A list of games that match the specified platform.
     */
    public List<Game> findByPlatform(String platformName) {
        String jpql = "SELECT g FROM Game g WHERE g.platform.name = :platformName";

        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("platformName", platformName);

        return query.getResultList();
    }
    
    /**
     * Finds all games with a rating greater than or equal to a given value.
     *
     * @param minRating The minimum rating value.
     * @return A list of games that meet the rating criteria.
     */
    public List<Game> findByRatingGreaterThan(Double minRating) {
        String jpql = "SELECT g FROM Game g WHERE g.rating >= :minRating ORDER BY g.rating DESC";

        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("minRating", minRating);

        return query.getResultList();
    }
}