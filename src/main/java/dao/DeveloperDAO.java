package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.Developer;

/**
 * Data Access Object (DAO) for the {@link Developer} entity.
 * It can also be used to implement custom, developer-specific query methods.
 *
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-07
 */
public class DeveloperDAO extends GenericDAO<Developer, Long> {

    public DeveloperDAO(EntityManager entityManager) {
        super(entityManager);
    }

    // --- Custom Query Methods ---

    /**
     * @param name The name of the developer to search for.
     * @return The found Developer entity, or {@code null} if no developer with that
     *         name
     *         exists.
     */
    public Developer findByName(String name) {
        String jpql = "SELECT p FROM Developer p WHERE p.name = :name";

        TypedQuery<Developer> query = entityManager.createQuery(jpql, Developer.class);
        query.setParameter("name", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Finds all developers belonging to a specific location.
     *
     * @param location The location to search for.
     * @return A list of developers that match the specified location.
     */
    public List<Developer> findByLocation(String location) {
        String jpql = "SELECT d FROM Developer d WHERE d.location = :location";

        TypedQuery<Developer> query = entityManager.createQuery(jpql, Developer.class);
        query.setParameter("location", location);

        return query.getResultList();
    }
}