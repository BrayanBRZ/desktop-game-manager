package dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.Platform;

public class PlatformDAO extends GenericDAO<Platform, Long> {

    public PlatformDAO() {
        super();
    }

    /**
     * @param name The name of the platform to search for.
     * @return The found Platform entity, or {@code null} if no platform with that name
     *         exists.
     */
    public Platform findByName(String name) {
        String jpql = "SELECT p FROM Platform p WHERE p.name = :name";

        TypedQuery<Platform> query = em.createQuery(jpql, Platform.class);
        query.setParameter("name", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}