package dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.Genre;

/**
 * Data Access Object (DAO) for the {@link Genre} entity.
 * It can also be used to implement custom, genre-specific query methods.
 * 
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-06
 */
public class GenreDAO extends GenericDAO<Genre, Long> {

    public GenreDAO(EntityManager entityManager) {
        super(entityManager);
    }

    // --- Custom Query Methods ---

    /**
     * @param name The name of the genre to search for.
     * @return The found Genre entity, or {@code null} if no genre with that name
     *         exists.
     */
    public Genre findByName(String name) {
        String jpql = "SELECT g FROM Genre g WHERE g.name = :name";

        TypedQuery<Genre> query = entityManager.createQuery(jpql, Genre.class);
        query.setParameter("name", name);

        try {
            // getSingleResult() throws an exception if no result is found.
            // We catch it and return null to provide a more user-friendly API.
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}