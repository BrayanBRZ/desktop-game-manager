package service;

import dao.DeveloperDAO;
import model.Developer;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Service layer for managing Developer entities. Encapsulates business logic,
 * validation, and transaction management for developers. This version operates
 * directly with entity fields instead of DTOs.
 */
public class DeveloperService {

    private final DeveloperDAO developerDAO = new DeveloperDAO();

    // #region CRUD Operations
    /**
     * Creates a new developer.
     *
     * @param name The developer's name.
     * @param location The developer's location (can be null).
     * @param symbolPath The path to the developer's symbol (can be null).
     * @return The newly created Developer entity.
     * @throws ValidationException if the data is invalid or the name is already
     * taken.
     * @throws ServiceException on database errors.
     */
    public Developer createDeveloper(String name, String location, String symbolPath) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Developer name cannot be empty.");
        }
        try {
            if (developerDAO.findByName(name.trim()) != null) {
                throw new ValidationException("A developer with the name '" + name + "' already exists.");
            }

            Developer newDeveloper = new Developer();
            newDeveloper.setName(name.trim());
            newDeveloper.setLocation(location);
            newDeveloper.setSymbolPath(symbolPath);

            developerDAO.save(newDeveloper);
            return newDeveloper;
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to create developer.", e);
        }
    }

    /**
     * Updates an existing developer.
     *
     * @param id The ID of the developer to update.
     * @param name The new name for the developer.
     * @param location The new location for the developer.
     * @param symbolPath The new symbol path for the developer.
     * @return The updated Developer entity.
     * @throws ValidationException if data is invalid or developer is not found.
     * @throws ServiceException on database errors.
     */
    public Developer updateDeveloper(Long id, String name, String location, String symbolPath) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Developer name cannot be empty.");
        }
        if (id == null) {
            throw new ValidationException("Developer ID is required for an update.");
        }
        try {
            Developer developerToUpdate = developerDAO.findById(id);
            if (developerToUpdate == null) {
                throw new ValidationException("Developer with ID " + id + " not found.");
            }

            // Check if the new name is being taken by another developer
            Developer existingDeveloperWithNewName = developerDAO.findByName(name.trim());
            if (existingDeveloperWithNewName != null && !existingDeveloperWithNewName.getId().equals(id)) {
                throw new ValidationException("Another developer with the name '" + name + "' already exists.");
            }

            developerToUpdate.setName(name.trim());
            developerToUpdate.setLocation(location);
            developerToUpdate.setSymbolPath(symbolPath);

            return developerDAO.update(developerToUpdate);
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to update developer with ID " + id, e);
        }
    }

    /**
     * Deletes a developer by its ID.
     *
     * @param id The ID of the developer to delete.
     * @throws ServiceException on database errors.
     */
    public void deleteById(Long id) throws ServiceException {
        try {
            developerDAO.delete(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to delete developer by ID: " + id, e);
        }
    }
    // #endregion

    // #region Finder Methods (Exposing DAO functionality)
    public Developer findById(Long id) throws ServiceException {
        try {
            return developerDAO.findById(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding developer by ID: " + id, e);
        }
    }

    public Developer findByName(String name) throws ServiceException {
        try {
            return developerDAO.findByName(name);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding developer by name: " + name, e);
        }
    }

    public List<Developer> findAll() throws ServiceException {
        try {
            return developerDAO.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding all developers.", e);
        }
    }

    public List<Developer> findByNameContaining(String searchTerm) throws ServiceException {
        try {
            return developerDAO.findByNameContaining(searchTerm);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding developers by search term: " + searchTerm, e);
        }
    }

    public List<Developer> findByDevelopedGameName(String gameName) throws ServiceException {
        try {
            return developerDAO.findByDevelopedGameName(gameName);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding developers by game name: " + gameName, e);
        }
    }

    public List<Developer> findByDevelopedGameId(Long gameId) throws ServiceException {
        try {
            return developerDAO.findByDevelopedGameId(gameId);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding developers by game ID: " + gameId, e);
        }
    }

    /**
     * Finds a developer by name. If it doesn't exist, a new one is created.
     *
     * @param name The name of the developer.
     * @return The existing or newly created Developer entity.
     * @throws ValidationException if the name is null or empty.
     * @throws ServiceException if a database error occurs.
     */
    public Developer createOrFind(String name) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Developer name cannot be empty.");
        }
        try {
            Developer existing = findByName(name);
            return existing != null ? existing : createDeveloper(name, null, null);
        } catch (ServiceException e) {
            // Re-throw the original service exception
            throw e;
        }
    }
    // #endregion
}
