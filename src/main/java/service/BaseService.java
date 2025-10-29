package service;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import util.JPAUtil;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseService {

    protected <R> R executeInTransaction(Function<EntityManager, R> action) throws ServiceException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = action.apply(em);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new ServiceException("Erro durante operação de banco de dados: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    protected void executeInTransaction(Consumer<EntityManager> action) throws ServiceException {
    executeInTransaction(em -> {
        action.accept(em);
        return null;
    });
}

    protected <R> R executeReadOnly(Function<EntityManager, R> action) throws ServiceException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return action.apply(em);
        } catch (Exception e) {
            throw new ServiceException("Erro durante consulta: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
