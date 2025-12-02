package dao;

import utils.MyLinkedList;

public interface IGenericDAO<T> {

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
    void delete(Long id);

    /**
     * Finds an entity by its primary key ID.
     *
     * @param id The ID of the entity to be found.
     * @return The found entity, or {@code null} if no entity with the given ID
     *         exists.
     */
    T findById(Long id);

    /**
     * Retrieves all instances of the entity type from the database.
     *
     * @return A list containing all entities of the specified type.
     */
    MyLinkedList<T> findAll();
}