package dao;

import javax.persistence.EntityManager;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Generic and abstract implementation of the IGenericDAO interface.
 * Contains common logic for CRUD operations with JPA.
 *
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-02
 *
 * @param <T> The type of the entity.
 * @param <K> The type of the primary key of the entity.
 */
public abstract class GenericDAO<T, K> implements IGenericDAO<T, K> {

    protected final EntityManager entityManager;
    private final Class<T> persistentClass;

    @SuppressWarnings("unchecked")
    public GenericDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.persistentClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    @Override
    public void save(T entity) {
        this.entityManager.persist(entity);
    }

    @Override
    public T update(T entity) {
        return this.entityManager.merge(entity);
    }

    @Override
    public void delete(K id) {
        T entity = findById(id);
        if (entity != null) {
            this.entityManager.remove(entity);
        }
    }

    @Override
    public T findById(K id) {
        return this.entityManager.find(persistentClass, id);
    }

    @Override
    public List<T> findAll() {
        String jpql = "FROM " + persistentClass.getName();
        return this.entityManager.createQuery(jpql, persistentClass).getResultList();
    }
}