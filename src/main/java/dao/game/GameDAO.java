package dao.game;

import model.game.Game;
import dao.GenericDAO;
import utils.MyLinkedList;
import javax.persistence.TypedQuery;

public class GameDAO extends GenericDAO<Game> {

    public GameDAO() {
        super(Game.class);
    }

    // #region Utility Methods
    public Game refreshAndClearAssociations(Game game) {
        return executeInTransaction(em -> {

            em.createNativeQuery("DELETE FROM game_genres WHERE game_id = :id")
                    .setParameter("id", game.getId()).executeUpdate();
            em.createNativeQuery("DELETE FROM game_platforms WHERE game_id = :id")
                    .setParameter("id", game.getId()).executeUpdate();
            em.createNativeQuery("DELETE FROM game_developers WHERE game_id = :id")
                    .setParameter("id", game.getId()).executeUpdate();

            em.clear(); // Limpa primeiro nível cache
            em.flush(); // Força escrita pendente

            // Busca novo game sem associações
            Game freshGame = em.find(Game.class, game.getId());
            freshGame.getGameGenres().clear();
            freshGame.getGamePlatforms().clear();
            freshGame.getGameDevelopers().clear();

            return freshGame;
        });
    }
    // #endregion Utility Methods

    // #region Exclusive Finders
    public MyLinkedList<Game> findByRatingGreaterThan(Double minRating) {
        return executeInTransaction(em -> {
            String jpql = "SELECT g FROM Game g WHERE g.rating >= :minRating ORDER BY g.rating DESC";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("minRating", minRating);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }
    // #endregion Exclusive Finders

    // #region Finders by RELATED ENTITY
    // Finders by Names
    public MyLinkedList<Game> findByGenreName(String genreName) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameGenres gg JOIN gg.genre genre WHERE genre.name = :genreName";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("genreName", genreName);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    public MyLinkedList<Game> findByPlatformName(String platformName) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gamePlatforms gp JOIN gp.platform platform WHERE platform.name = :platformName";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("platformName", platformName);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    public MyLinkedList<Game> findByDeveloperName(String developerName) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameDevelopers gd JOIN gd.developer developer WHERE developer.name = :developerName";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("developerName", developerName);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    // Finders by IDs
    public MyLinkedList<Game> findByGenreId(Long genreId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameGenres gg JOIN gg.genre genre WHERE genre.id = :genreId";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("genreId", genreId);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    public MyLinkedList<Game> findByPlatformId(Long platformId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gamePlatforms gp JOIN gp.platform platform WHERE platform.id = :platformId";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("platformId", platformId);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    public MyLinkedList<Game> findByDeveloperId(Long developerId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT g FROM Game g JOIN g.gameDevelopers gd JOIN gd.developer developer WHERE developer.id = :developerId";
            TypedQuery<Game> query = em.createQuery(jpql, Game.class);
            query.setParameter("developerId", developerId);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }
    // #endregion Finders by RELATED ENTITY
}
