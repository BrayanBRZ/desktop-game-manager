package dao;

import model.User;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class UserDAO extends GenericDAO<User, Long> {

    // #region Finders by Username/Name
    /**
     * Finds a single User entity by its exact username.
     *
     * @param username The username to search for.
     * @return The found User entity, or {@code null} if it does not exist.
     */
    public User findByUsername(String username) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.username = :username";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Finds a list of users whose usernames contain the given search term
     * (case-insensitive).
     *
     * @param searchTerm The text to search for within the usernames.
     * @return A list of users that match the search criteria.
     */
    public List<User> findByNameContaining(String searchTerm) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(:searchTerm)";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            String searchTermWithWildcards = "%" + (searchTerm == null ? "" : searchTerm) + "%";
            query.setParameter("searchTerm", searchTermWithWildcards);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion

    // #region Finders by Profile Data
    /**
     * Finds all users born on a specific date.
     *
     * @param birthDate The exact birth date to search for.
     * @return A list of users born on that date.
     */
    public List<User> findByBirthDate(LocalDate birthDate) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT u FROM User u WHERE u.birthDate = :birthDate";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("birthDate", birthDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds all users who are currently a specific age. This is calculated
     * based on their birth date.
     *
     * @param age The age to search for.
     * @return A list of users with that specific age.
     */
    public List<User> findByAge(int age) {
        EntityManager em = factory.createEntityManager();
        try {
            // Logic to calculate the date range for a given age
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusYears(age + 1).plusDays(1);
            LocalDate endDate = today.minusYears(age);

            String jpql = "SELECT u FROM User u WHERE u.birthDate BETWEEN :startDate AND :endDate";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion

    // #region Finders by Game Library
    /**
     * Finds all users who have a specific game in their library, searching by
     * the game's name.
     *
     * @param gameName The name of the game to search for in user libraries.
     * @return A list of users who own the game.
     */
    public List<User> findByGameName(String gameName) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT u FROM User u JOIN u.userGames ug WHERE ug.game.name = :gameName";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("gameName", gameName);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds all users who have a specific game in their library, searching by
     * the game's ID.
     *
     * @param gameId The ID of the game to search for in user libraries.
     * @return A list of users who own the game.
     */
    public List<User> findByGameId(Long gameId) {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "SELECT DISTINCT u FROM User u JOIN u.userGames ug WHERE ug.game.id = :gameId";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("gameId", gameId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    // #endregion
}
