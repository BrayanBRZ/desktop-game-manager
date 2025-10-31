package dao;

import model.Game;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

public class GameDAO extends GenericDAO<Game, Long> {

    // Constructor
    public GameDAO(EntityManager em) {
        super(em);
    }

    // #region Exclusive Finders
    public List<Game> findByRatingGreaterThan(Double minRating) {
        String jpql = "SELECT g FROM Game g WHERE g.rating >= :minRating ORDER BY g.rating DESC";
        TypedQuery<Game> query = em.createQuery(jpql, Game.class);
        query.setParameter("minRating", minRating);
        return query.getResultList();
    }
    // #endregion Exclusive Finders

    // #region Finders by RELATED ENTITY
    // Finders by Names
    public List<Game> findByGenreName(String genreName) {
        String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameGenres gg JOIN gg.genre genre WHERE genre.name = :genreName";
        TypedQuery<Game> query = em.createQuery(jpql, Game.class);
        query.setParameter("genreName", genreName);
        return query.getResultList();
    }

    public List<Game> findByPlatformName(String platformName) {
        String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gamePlatforms gp JOIN gp.platform platform WHERE platform.name = :platformName";
        TypedQuery<Game> query = em.createQuery(jpql, Game.class);
        query.setParameter("platformName", platformName);
        return query.getResultList();
    }

    public List<Game> findByDeveloperName(String developerName) {
        String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameDevelopers gd JOIN gd.developer developer WHERE developer.name = :developerName";
        TypedQuery<Game> query = em.createQuery(jpql, Game.class);
        query.setParameter("developerName", developerName);
        return query.getResultList();
    }

    // Finders by IDs
    public List<Game> findByGenreId(Long genreId) {
        String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameGenres gg JOIN gg.genre genre WHERE genre.id = :genreId";
        TypedQuery<Game> query = em.createQuery(jpql, Game.class);
        query.setParameter("genreId", genreId);
        return query.getResultList();
    }

    public List<Game> findByPlatformId(Long platformId) {
        String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gamePlatforms gp JOIN gp.platform platform WHERE platform.id = :platformId";
        TypedQuery<Game> query = em.createQuery(jpql, Game.class);
        query.setParameter("platformId", platformId);
        return query.getResultList();
    }

    public List<Game> findByDeveloperId(Long developerId) {
        String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameDevelopers gd JOIN gd.developer developer WHERE developer.id = :developerId";
        TypedQuery<Game> query = em.createQuery(jpql, Game.class);
        query.setParameter("developerId", developerId);
        return query.getResultList();
    }
    // #endregion Finders by RELATED ENTITY
}
