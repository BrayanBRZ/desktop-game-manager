package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.EntityTransaction;

import java.util.function.Consumer;
import java.util.function.Function;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @param <T> The type of the entity.
 * @param <K> The type of the primary key of the entity.
 */
public abstract class GenericDAO<T, K> implements IGenericDAO<T, K> {

    private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("desktop-game-manager");

    private final Class<T> persistentClass;

    protected final EntityManager em;

    // Constructor
    @SuppressWarnings("unchecked")
    public GenericDAO() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
        this.em = FACTORY.createEntityManager();
    }

    // #region CRUD Methods
    @Override
    public void save(T entity) {
        em.persist(entity);
    }

    @Override
    public T update(T entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(K id) {
        T entity = em.find(persistentClass, id);
        if (entity != null) {
            em.remove(entity);
        }
    }
    // #endregion CRUD Methods

    // #region Read-only Methods
    @Override
    public T findById(K id) {
        return em.find(persistentClass, id);
    }

    @Override
    public List<T> findAll() {
        String jpql = "FROM " + persistentClass.getName();
        return em.createQuery(jpql, persistentClass).getResultList();
    }

    public T findByName(String name) {
        try {
            String jpql = "SELECT t FROM " + persistentClass.getName() + " t WHERE t.name = :name";
            TypedQuery<T> query = em.createQuery(jpql, persistentClass);
            query.setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> findByNameContaining(String searchTerm) {
        String jpql = "SELECT t FROM " + persistentClass.getName() + " t WHERE LOWER(t.name) LIKE LOWER(:searchTerm)";
        TypedQuery<T> query = em.createQuery(jpql, persistentClass);
        String searchTermWithWildcards = "%" + (searchTerm == null ? "" : searchTerm) + "%";
        query.setParameter("searchTerm", searchTermWithWildcards);
        return query.getResultList();
    }
    // #endregion Read-only Methods

    // #region Transaction & Execution Control
    public <R> R executeInTransaction(Function<EntityManager, R> action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = action.apply(em);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao executar transação", e);
        }
    }

    public void performInTransaction(Consumer<EntityManager> action) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erro ao executar transação", e);
        }
    }

    public <R> R executeReadOnly(Function<EntityManager, R> action) {
        try {
            return action.apply(em);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao executar operação de leitura", e);
        }
    }

    public void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
    }
    // #endregion Transaction & Execution Control
}
