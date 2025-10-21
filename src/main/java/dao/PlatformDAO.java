package dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import model.Platform;

public class PlatformDAO extends GenericDAO<Platform, Long> {

    // #region Finders by NAME
    /**
     * Finds a single Platform entity by its exact name.
     * 
     * @param name The name of the platform to search for.
     * @return The found Platform entity, or {@code null} if it does not exist.
     */
    public Platform findByName(String name) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT p FROM Platform p WHERE p.name = :name";
            TypedQuery<Platform> query = em.createQuery(jpql, Platform.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a list of platforms whose names contain the given search term
     * (case-insensitive).
     * 
     * @param searchTerm The text to search for within the platform names.
     * @return A list of platforms that match the search criteria.
     */
    public List<Platform> findByNameContaining(String searchTerm) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT p FROM Platform p WHERE LOWER(p.name) LIKE LOWER(:searchTerm)";
            TypedQuery<Platform> query = em.createQuery(jpql, Platform.class);
            String searchTermWithWildcards = "%" + (searchTerm == null ? "" : searchTerm) + "%";
            query.setParameter("searchTerm", searchTermWithWildcards);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion Finders by NAME
}