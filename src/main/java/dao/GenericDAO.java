package dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * @param <T> The type of the entity.
 * @param <K> The type of the primary key of the entity.
 */
public abstract class GenericDAO<T, K> implements IGenericDAO<T, K> {

    protected static final EntityManagerFactory factory;

    static {
        factory = Persistence.createEntityManagerFactory("desktopgamemanager");
    }

    private final Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    public GenericDAO() {
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    @Override
    public void save(T entity) {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T update(T entity) {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        T updatedEntity = null;
        try {
            transaction.begin();
            updatedEntity = em.merge(entity);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
        return updatedEntity;
    }

    @Override
    public void delete(K id) {
        EntityManager em = factory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            T entity = em.find(persistentClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // --- Read-only methods ---
    @Override
    public T findById(K id) {
        EntityManager em = factory.createEntityManager();
        try {
            return em.find(persistentClass, id);
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = factory.createEntityManager();
        try {
            String jpql = "FROM " + persistentClass.getName();
            return em.createQuery(jpql, persistentClass).getResultList();
        } finally {
            em.close();
        }
    }
}
