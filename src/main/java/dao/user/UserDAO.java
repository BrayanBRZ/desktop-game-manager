package dao.user;

import model.user.User;
import dao.GenericDAO;
import utils.MyLinkedList;
import java.time.LocalDate;
import javax.persistence.TypedQuery;

public class UserDAO extends GenericDAO<User> {

    public UserDAO() {
        super(User.class);
    }

    // #region Finders by Profile Data
    public MyLinkedList<User> findByBirthDate(LocalDate birthDate) {
        return executeInTransaction(em -> {
            String jpql = "SELECT u FROM User u WHERE u.birthDate = :birthDate";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("birthDate", birthDate);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    public MyLinkedList<User> findByAge(int age) {
        return executeInTransaction(em -> {
            LocalDate today = LocalDate.now();
            LocalDate startDate = today.minusYears(age + 1).plusDays(1);
            LocalDate endDate = today.minusYears(age);

            String jpql = "SELECT u FROM User u WHERE u.birthDate BETWEEN :startDate AND :endDate";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }
    // #endregion Finders by Profile Data

    // #region Finders by RELATED ENTITY
    public MyLinkedList<User> findByGameName(String gameName) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT u FROM User u JOIN u.userGames ug WHERE ug.game.name = :gameName";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("gameName", gameName);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }

    public MyLinkedList<User> findByGameId(Long gameId) {
        return executeInTransaction(em -> {
            String jpql = "SELECT DISTINCT u FROM User u JOIN u.userGames ug WHERE ug.game.id = :gameId";
            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("gameId", gameId);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }
    // #endregion Finders by RELATED ENTITY
}
