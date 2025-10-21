package dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import model.Genre;

public class GenreDAO extends GenericDAO<Genre, Long> {

    // #region Finders by NAME
    /**
     * Finds a single Genre entity by its exact name.
     * 
     * @param name The name of the genre to search for.
     * @return The found Genre entity, or {@code null} if it does not exist.
     */
    public Genre findByName(String name) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT g FROM Genre g WHERE g.name = :name";
            TypedQuery<Genre> query = em.createQuery(jpql, Genre.class);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a list of genres whose names contain the given search term
     * (case-insensitive).
     * 
     * @param searchTerm The text to search for within the genre names.
     * @return A list of genres that match the search criteria.
     */
    public List<Genre> findByNameContaining(String searchTerm) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT g FROM Genre g WHERE LOWER(g.name) LIKE LOWER(:searchTerm)";
            TypedQuery<Genre> query = em.createQuery(jpql, Genre.class);
            String searchTermWithWildcards = "%" + (searchTerm == null ? "" : searchTerm) + "%";
            query.setParameter("searchTerm", searchTermWithWildcards);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion Finders by NAME
}