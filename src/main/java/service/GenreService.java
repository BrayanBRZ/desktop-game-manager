package service;

import dao.GenreDAO;
import model.Genre;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Service layer for managing Genre entities.
 * Encapsulates business logic, validation, and transaction management for genres.
 */
public class GenreService {

    private final GenreDAO genreDAO = new GenreDAO();

    // #region CRUD Operations
    /**
     * Creates a new genre.
     * @param name The genre's name.
     * @return The newly created Genre entity.
     * @throws ValidationException if the data is invalid or the name is already taken.
     * @throws ServiceException on database errors.
     */
    public Genre createGenre(String name) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Genre name cannot be empty.");
        }
        try {
            if (genreDAO.findByName(name.trim()) != null) {
                throw new ValidationException("A genre with the name '" + name + "' already exists.");
            }

            Genre newGenre = new Genre(name.trim());
            genreDAO.save(newGenre);
            return newGenre;
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to create genre.", e);
        }
    }

    /**
     * Updates an existing genre.
     * @param id The ID of the genre to update.
     * @param name The new name for the genre.
     * @return The updated Genre entity.
     * @throws ValidationException if data is invalid or genre is not found.
     * @throws ServiceException on database errors.
     */
    public Genre updateGenre(Long id, String name) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Genre name cannot be empty.");
        }
        if (id == null) {
            throw new ValidationException("Genre ID is required for an update.");
        }
        try {
            Genre genreToUpdate = genreDAO.findById(id);
            if (genreToUpdate == null) {
                throw new ValidationException("Genre with ID " + id + " not found.");
            }
            
            // Check if the new name is being taken by another genre
            Genre existingGenreWithNewName = genreDAO.findByName(name.trim());
            if (existingGenreWithNewName != null && !existingGenreWithNewName.getId().equals(id)) {
                throw new ValidationException("Another genre with the name '" + name + "' already exists.");
            }

            genreToUpdate.setName(name.trim());
            return genreDAO.update(genreToUpdate);
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to update genre with ID " + id, e);
        }
    }

    /**
     * Deletes a genre by its ID.
     * @param id The ID of the genre to delete.
     * @throws ServiceException on database errors.
     */
    public void deleteById(Long id) throws ServiceException {
        try {
            genreDAO.delete(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to delete genre by ID: " + id, e);
        }
    }
    // #endregion

    // #region Finder Methods
    
    public Genre findById(Long id) throws ServiceException {
        try {
            return genreDAO.findById(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding genre by ID: " + id, e);
        }
    }

    public Genre findByName(String name) throws ServiceException {
        try {
            return genreDAO.findByName(name);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding genre by name: " + name, e);
        }
    }
    
    public List<Genre> findAll() throws ServiceException {
        try {
            return genreDAO.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding all genres.", e);
        }
    }

    public List<Genre> findByNameContaining(String searchTerm) throws ServiceException {
        try {
            return genreDAO.findByNameContaining(searchTerm);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding genres by search term: " + searchTerm, e);
        }
    }
    
    /**
     * Finds a genre by name. If it doesn't exist, a new one is created.
     * @param name The name of the genre.
     * @return The existing or newly created Genre entity.
     * @throws ValidationException if the name is null or empty.
     * @throws ServiceException on database errors.
     */
    public Genre createOrFind(String name) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Genre name cannot be empty.");
        }
        try {
            Genre existing = findByName(name);
            return existing != null ? existing : createGenre(name);
        } catch(ServiceException e) {
            throw e;
        }
    }
    // #endregion
}