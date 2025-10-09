package dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.Developer;

public class DeveloperDAO extends GenericDAO<Developer, Long> {

    public DeveloperDAO() {
        super();
    }

    /**
     * @param name The name of the developer to search for.
     * @return The found Developer entity, or {@code null} if no developer with that
     *         name exists.
     */
    public Developer findByName(String name) {
        EntityManager em = factory.createEntityManager();
        String jpql = "SELECT p FROM Developer p WHERE p.name = :name";

        TypedQuery<Developer> query = em.createQuery(jpql, Developer.class);
        query.setParameter("name", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param location The location to search for.
     * @return A list of developers that match the specified location.
     */
    public List<Developer> findByLocation(String location) {
        EntityManager em = factory.createEntityManager();
        String jpql = "SELECT d FROM Developer d WHERE d.location = :location";

        TypedQuery<Developer> query = em.createQuery(jpql, Developer.class);
        query.setParameter("location", location);

        return query.getResultList();
    }
}