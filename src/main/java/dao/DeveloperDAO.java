package dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import model.Developer;

public class DeveloperDAO extends GenericDAO<Developer, Long> {

    // #region Finders by NAME
    /**
     * Finds a single Developer entity by its exact name.
     * 
     * @param name The name of the developer to search for.
     * @return The found Developer entity, or {@code null} if it does not exist.
     */
    public Developer findByName(String name) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT d FROM Developer d WHERE d.name = :name";
            TypedQuery<Developer> query = em.createQuery(jpql, Developer.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a list of developers whose names contain the given search term
     * (case-insensitive).
     * 
     * @param searchTerm The text to search for within the developer names.
     * @return A list of developers that match the search criteria.
     */
    public List<Developer> findByNameContaining(String searchTerm) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT d FROM Developer d WHERE LOWER(d.name) LIKE LOWER(:searchTerm)";
            TypedQuery<Developer> query = em.createQuery(jpql, Developer.class);
            String searchTermWithWildcards = "%" + (searchTerm == null ? "" : searchTerm) + "%";
            query.setParameter("searchTerm", searchTermWithWildcards);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion Finders by NAME

    // #region Finders by RELATED ENTITY
    /**
     * Finds all developers that worked on a game with the given name.
     * 
     * @param gameName The name of the developed game to search for.
     * @return A list of developers that match the specified game.
     */
    public List<Developer> findByDevelopedGameName(String gameName) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT d FROM Developer d JOIN d.developedGames gd JOIN gd.game g WHERE g.name = :gameName";
            TypedQuery<Developer> query = em.createQuery(jpql, Developer.class);
            query.setParameter("gameName", gameName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds all developers that worked on a game with the given ID.
     * 
     * @param gameId The ID of the developed game to search for.
     * @return A list of developers that match the specified game.
     */
    public List<Developer> findByDevelopedGameId(Long gameId) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT d FROM Developer d JOIN d.developedGames gd JOIN gd.game g WHERE g.id = :gameId";
            TypedQuery<Developer> query = em.createQuery(jpql, Developer.class);
            query.setParameter("gameId", gameId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion Finders by RELATED ENTITY
}