package dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.Genre;

public class GenreDAO extends GenericDAO<Genre, Long> {

    /**
     * Constructs a new GenreDAO with the given EntityManager.
     *
     * @param entityManager The EntityManager to be used for database operations.
     */
    public GenreDAO(EntityManager entityManager) {
        super(entityManager);
    }

    // --- Custom Query Methods ---

    /**
     * Finds a single Genre entity by its exact name.
     * This method is useful for checking if a genre already exists before creating
     * it.
     *
     * @param name The name of the genre to search for.
     * @return The found Genre entity, or {@code null} if no genre with that name
     *         exists.
     */
    public Genre findByName(String name) {
        // JPQL query to select a genre where the name matches the parameter.
        String jpql = "SELECT g FROM Genre g WHERE g.name = :name";

        TypedQuery<Genre> query = entityManager.createQuery(jpql, Genre.class);
        query.setParameter("name", name);

        try {
            // getSingleResult() throws an exception if no result is found.
            // We catch it and return null to provide a more user-friendly API.
            return query.getSingleResult();
        } catch (NoResultException e) {
            // This is not an error, it simply means the genre was not found.
            return null;
        }
    }
}