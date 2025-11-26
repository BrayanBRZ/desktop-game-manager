package dao;

import javax.persistence.TypedQuery;

import model.user.User;

import java.time.LocalDate;
import java.util.List;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    // #region Finders by Profile Data
    public List<User> findByBirthDate(LocalDate birthDate) {
        return executeInTransaction(em -> {
            String jpql = "SELECT u FROM User u WHERE u.birthDate = :birthDate";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("birthDate", birthDate);
            return query.getResultList();
        });
    }

    public List<User> findByAge(int age) {
        return executeInTransaction(em -> {
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusYears(age + 1).plusDays(1);
            LocalDate endDate = today.minusYears(age);

            String jpql = "SELECT u FROM User u WHERE u.birthDate BETWEEN :startDate AND :endDate";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return query.getResultList();
        });
    }
    // #endregion Finders by Profile Data

    // #region Finders by RELATED ENTITY
    public List<User> findByGameName(String gameName) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT u FROM User u JOIN u.userGames ug WHERE ug.game.name = :gameName";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("gameName", gameName);
            return query.getResultList();
        });
    }

    public List<User> findByGameId(Long gameId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT u FROM User u JOIN u.userGames ug WHERE ug.game.id = :gameId";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("gameId", gameId);
            return query.getResultList();
        });
    }
    // #endregion Finders by RELATED ENTITY
}
