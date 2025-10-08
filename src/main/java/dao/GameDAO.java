package dao;

import model.Game;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Data Access Object (DAO) for the {@link Game} entity.
 * It can also be used to implement custom, game-specific query methods.
 *
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-02
 */
public class GameDAO extends GenericDAO<Game, Long> {

    public GameDAO(EntityManager entityManager) {
        super(entityManager);
    }

    // --- Custom Query Methods ---

    //#region Exclusive Finders

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

    //#endregion

    // #region Finders by NAME

    /**
     * @param name The name of the game to search for.
     * @return The found Game entity, or {@code null} if no game with that name
     *         exists.
     */
    public Game findByExactName(String name) {
        String jpql = "SELECT g FROM Game g WHERE g.name = :name";

        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("name", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Finds a list of games whose names contain the given search term.
     * The search is case-insensitive and matches partial names.
     *
     * @param searchTerm The text to search for within the game names.
     * @return A list of games that match the search criteria.
     */
    public List<Game> findByNameContaining(String searchTerm) {
        String jpql = "SELECT g FROM Game g WHERE LOWER(g.name) LIKE LOWER(:searchTerm)";

        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        String searchTermWithWildcards = "%" + searchTerm + "%";
        query.setParameter("searchTerm", searchTermWithWildcards);

        return query.getResultList();
    }

    /**
     * Finds all games belonging to a specific genre.
     *
     * @param genreName The name of the genre to search for.
     * @return A list of games that match the specified genre.
     */
    public List<Game> findByGenre(String genreName) {
        String jpql = "SELECT g FROM Game g JOIN g.genres genre WHERE genre.name = :genreName";

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
        String jpql = "SELECT g FROM Game g JOIN g.platforms platform WHERE platform.name = :platformName";

        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("platformName", platformName);

        return query.getResultList();
    }

    /**
     * Finds all games available on a specific developer.
     *
     * @param developerName The name of the developer to search for.
     * @return A list of games that match the specified developer.
     */
    public List<Game> findByDeveloper(String developerName) {
        String jpql = "SELECT g FROM Game g  JOIN g.developers developer WHERE developer.name = :developerName";

        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("developerName", developerName);

        return query.getResultList();
    }

    // #endregion

    // #region Finders by ID

    /**
     * Finds all games belonging to a specific genre by its ID.
     *
     * @param genreId The ID of the genre to search for.
     * @return A list of games that match the specified genre ID.
     */
    public List<Game> findByGenreId(Long genreId) {
        String jpql = "SELECT g FROM Game g JOIN g.genres genre WHERE genre.id = :genreId";
        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("genreId", genreId);
        return query.getResultList();
    }

    /**
     * Finds all games available on a specific platform by its ID.
     *
     * @param platformId The ID of the platform to search for.
     * @return A list of games that match the specified platform ID.
     */
    public List<Game> findByPlatformId(Long platformId) {
        String jpql = "SELECT g FROM Game g JOIN g.platforms platform WHERE platform.id = :platformId";
        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("platformId", platformId);
        return query.getResultList();
    }

    /**
     * Finds all games available on a specific developer by its ID.
     *
     * @param developerId The ID of the developer to search for.
     * @return A list of games that match the specified developer ID.
     */
    public List<Game> findByDeveloperId(Long developerId) {
        String jpql = "SELECT g FROM Game g JOIN g.developers developer WHERE developer.id = :developerId";
        TypedQuery<Game> query = entityManager.createQuery(jpql, Game.class);
        query.setParameter("developerId", developerId);
        return query.getResultList();
    }

    // #endregion
}