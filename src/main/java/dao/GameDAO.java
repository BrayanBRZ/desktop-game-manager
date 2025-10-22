package dao;

import model.Game;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;

public class GameDAO extends GenericDAO<Game, Long> {

    // #region Exclusive Finders
    public List<Game> findByRatingGreaterThan(Double minRating) {
        EntityManager em = factory.createEntityManager();

        try {
            String jpql = "SELECT g FROM Game g WHERE g.rating >= :minRating ORDER BY g.rating DESC";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("minRating", minRating);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion

    // #region Finders by NAME
    /**
     * @param name The name of the game to search for.
     * @return The found Game entity, or {@code null} if no game with that name
     *         exists.
     */
    public Game findByName(String name) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT g FROM Game g WHERE g.name = :name";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * @param searchTerm The text to search for within the game names.
     * @return A list of games that match the search criteria.
     */
    public List<Game> findByNameContaining(String searchTerm) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT g FROM Game g WHERE LOWER(g.name) LIKE :searchTerm";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            String searchTermWithWildcards = "%" + (searchTerm == null ? "" : searchTerm.toLowerCase()) + "%";
            query.setParameter("searchTerm", searchTermWithWildcards);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion

    // #region Finders by RELATED ENTITY NAME
    /**
     * @param genreName The name of the genre to search for.
     * @return A list of games that match the specified genre.
     */
    public List<Game> findByGenre(String genreName) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameGenres gg JOIN gg.genre genre WHERE genre.name = :genreName";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("genreName", genreName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * @param platformName The name of the platform to search for.
     * @return A list of games that match the specified platform.
     */
    public List<Game> findByPlatform(String platformName) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gamePlatforms gp JOIN gp.platform platform WHERE platform.name = :platformName";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("platformName", platformName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * @param developerName The name of the developer to search for.
     * @return A list of games that match the specified developer.
     */
    public List<Game> findByDeveloper(String developerName) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameDevelopers gd JOIN gd.developer developer WHERE developer.name = :developerName";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("developerName", developerName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion

    // #region Finders by RELATED ENTITY ID
    /**
     * @param genreId The ID of the genre to search for.
     * @return A list of games that match the specified genre ID.
     */
    public List<Game> findByGenreId(Long genreId) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameGenres gg JOIN gg.genre genre WHERE genre.id = :genreId";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("genreId", genreId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * @param platformId The ID of the platform to search for.
     * @return A list of games that match the specified platform ID.
     */
    public List<Game> findByPlatformId(Long platformId) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gamePlatforms gp JOIN gp.platform platform WHERE platform.id = :platformId";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("platformId", platformId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * @param developerId The ID of the developer to search for.
     * @return A list of games that match the specified developer ID.
     */
    public List<Game> findByDeveloperId(Long developerId) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameDevelopers gd JOIN gd.developer developer WHERE developer.id = :developerId";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("developerId", developerId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion
}