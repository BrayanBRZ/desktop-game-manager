package dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import model.Genre;

public class GenreDAO extends GenericDAO<Genre, Long> {

    public GenreDAO() {
        super();
    }

    /**
     * @param name The name of the genre to search for.
     * @return The found Genre entity, or {@code null} if no genre with that name
     *         exists.
     */
    public Genre findByName(String name) {
        EntityManager em = factory.createEntityManager();
        String jpql = "SELECT g FROM Genre g WHERE g.name = :name";

        TypedQuery<Genre> query = em.createQuery(jpql, Genre.class);
        query.setParameter("name", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}