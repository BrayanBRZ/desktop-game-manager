package dao;

import model.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 * Data Access Object (DAO) for the {@link User} entity.
 * This class extends the generic DAO and provides access to common
 * CRUD operations. It also includes custom finders, such as finding a
 * user by their username.
 *
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-12
 */
public class UserDAO extends GenericDAO<User, Long> {

    /**
     * Constructs a new UserDAO.
     */
    public UserDAO() {
        super();
    }

    // ----------------------------------------------------------------------------------
    // Custom query methods specific to the User entity can be added below.
    // ----------------------------------------------------------------------------------

    /**
     * Finds a single User entity by its exact username.
     * This method is useful for login checks and for preventing duplicate usernames
     * during registration.
     *
     * @param username The username to search for.
     * @return The found User entity, or {@code null} if no user with that username exists.
     */
    public User findByUsername(String username) {
        EntityManager em = factory.createEntityManager();
        try {
            // JPQL query to select a user where the username matches the parameter.
            String jpql = "SELECT u FROM User u WHERE u.username = :username";

            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("username", username);

            // getSingleResult() throws an exception if no result is found.
            // We catch it and return null to provide a more convenient API.
            return query.getSingleResult();
        } catch (NoResultException e) {
            // This is not an error, it simply means the user was not found.
            return null;
        } finally {
            em.close();
        }
    }
}