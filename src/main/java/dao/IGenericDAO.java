package dao;

import java.util.List;

public interface IGenericDAO<T, K> {

    /**
     * Persists a new entity to the database.
     *
     * @param entity The entity to be saved.
     */
    void save(T entity);

    /**
     * Updates an existing entity in the database.
     *
     * @param entity The entity to be updated.
     * @return The updated entity.
     */
    T update(T entity);

    /**
     * Removes an entity from the database by its primary key ID.
     *
     * @param id The ID of the entity to be removed.
     */
    void delete(K id);

    /**
     * Finds an entity by its primary key ID.
     *
     * @param id The ID of the entity to be found.
     * @return The found entity, or {@code null} if no entity with the given ID
     *         exists.
     */
    T findById(K id);

    /**
     * Retrieves all instances of the entity type from the database.
     *
     * @return A list containing all entities of the specified type.
     */
    List<T> findAll();
}