package dao;

import model.User;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class UserDAO extends GenericDAO<User, Long> {

    // Constructor
    public UserDAO(EntityManager em) {
        super(em);
    }

    // #region Finders by Profile Data
    public List<User> findByBirthDate(LocalDate birthDate) {
        String jpql = "SELECT u FROM User u WHERE u.birthDate = :birthDate";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("birthDate", birthDate);
        return query.getResultList();
    }

    public List<User> findByAge(int age) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusYears(age + 1).plusDays(1);
        LocalDate endDate = today.minusYears(age);

        String jpql = "SELECT u FROM User u WHERE u.birthDate BETWEEN :startDate AND :endDate";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    // #endregion Finders by Profile Data

    // #region Finders by RELATED ENTITY
    public List<User> findByGameName(String gameName) {
        String jpql = "SELECT DISTINCT u FROM User u JOIN u.userGames ug WHERE ug.game.name = :gameName";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("gameName", gameName);
        return query.getResultList();
    }

    public List<User> findByGameId(Long gameId) {
        String jpql = "SELECT DISTINCT u FROM User u JOIN u.userGames ug WHERE ug.game.id = :gameId";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("gameId", gameId);
        return query.getResultList();
    }
    // #endregion Finders by RELATED ENTITY
}
