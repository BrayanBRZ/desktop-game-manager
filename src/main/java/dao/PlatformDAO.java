package dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.Platform;

/**
 * Data Access Object (DAO) for the {@link Platform} entity.
 * It can also be used to implement custom, platform-specific query methods.
 *
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-07
 */
public class PlatformDAO extends GenericDAO<Platform, Long> {

    public PlatformDAO(EntityManager entityManager) {
        super(entityManager);
    }

    // --- Custom Query Methods ---

    /**
     * @param name The name of the platform to search for.
     * @return The found Platform entity, or {@code null} if no platform with that name
     *         exists.
     */
    public Platform findByName(String name) {
        String jpql = "SELECT p FROM Platform p WHERE p.name = :name";

        TypedQuery<Platform> query = entityManager.createQuery(jpql, Platform.class);
        query.setParameter("name", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}