package dao;

import utils.MyLinkedList;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.EntityTransaction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class GenericDAO<T> implements IGenericDAO<T> {

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("desktop-game-manager");

    private final Class<T> persistentClass;

    // Constructor
    public GenericDAO(Class<T> persistentClass) {
        this.persistentClass = persistentClass;
    }

    // #region Transaction & Execution Control
    public <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityManager em = FACTORY.createEntityManager();
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
            throw new RuntimeException("Erro ao executar transação", e);
        } finally {
            em.close();
        }
    }

    public void performInTransaction(Consumer<EntityManager> action) {
        executeInTransaction(em -> {
            action.accept(em);
            return null;
        });
    }

    public <R> R executeReadOnly(Function<EntityManager, R> action) {
        EntityManager em = FACTORY.createEntityManager();
        try {
            return action.apply(em);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar operação de leitura", e);
        } finally {
            em.close();
        }
    }
    // #endregion Transaction & Execution Control

    // #region CRUD Methods
    @Override
    public void save(T entity) {
        executeInTransaction(em -> {
            em.persist(entity);
            return null;
        });
    }

    @Override
    public T update(T entity) {
        return executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(Long id) {
        performInTransaction(em -> {
            T entity = em.find(persistentClass, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }
    // #endregion CRUD Methods

    // #region Read-only Methods
    @Override
    public T findById(Long id) {
        return executeReadOnly(em -> em.find(persistentClass, id));
    }

    @Override
    public MyLinkedList<T> findAll() {
        return executeReadOnly(em -> MyLinkedList.fromJavaList(
                em.createQuery("FROM " + persistentClass.getName(), persistentClass).getResultList()
        ));
    }

    public T findByName(String name) {
        return executeReadOnly(em -> {
            try {
                String jpql = "SELECT t FROM " + persistentClass.getName() + " t WHERE t.name = :name";
                TypedQuery<T> query = em.createQuery(jpql, persistentClass);
                query.setParameter("name", name);
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        });
    }

    public MyLinkedList<T> findByNameContaining(String searchTerm) {
        return executeReadOnly(em -> {
            String jpql = "SELECT t FROM " + persistentClass.getName()
                    + " t WHERE LOWER(t.name) LIKE LOWER(:searchTerm)";
            TypedQuery<T> query = em.createQuery(jpql, persistentClass);
            String searchTermWithWildcards = "%" + (searchTerm == null ? "" : searchTerm) + "%";
            query.setParameter("searchTerm", searchTermWithWildcards);
            return MyLinkedList.fromJavaList(query.getResultList());
        });
    }
    // #endregion Read-only Methods
}
