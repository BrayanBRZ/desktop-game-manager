package service;

import dao.PlatformDAO;
import model.Platform;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Service layer for managing Platform entities.
 * Encapsulates business logic, validation, and transaction management for platforms.
 */
public class PlatformService {

    private final PlatformDAO platformDAO = new PlatformDAO();

    // #region CRUD Operations
    /**
     * Creates a new platform.
     * @param name The platform's name.
     * @param symbolPath The path to the platform's symbol (can be null).
     * @return The newly created Platform entity.
     * @throws ValidationException if the data is invalid or the name is already taken.
     * @throws ServiceException on database errors.
     */
    public Platform createPlatform(String name, String symbolPath) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Platform name cannot be empty.");
        }
        try {
            if (platformDAO.findByName(name.trim()) != null) {
                throw new ValidationException("A platform with the name '" + name + "' already exists.");
            }

            Platform newPlatform = new Platform(name.trim());
            newPlatform.setSymbolPath(symbolPath);
            platformDAO.save(newPlatform);
            return newPlatform;
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to create platform.", e);
        }
    }

    /**
     * Updates an existing platform.
     * @param id The ID of the platform to update.
     * @param name The new name for the platform.
     * @param symbolPath The new symbol path for the platform.
     * @return The updated Platform entity.
     * @throws ValidationException if data is invalid or platform is not found.
     * @throws ServiceException on database errors.
     */
    public Platform updatePlatform(Long id, String name, String symbolPath) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Platform name cannot be empty.");
        }
        if (id == null) {
            throw new ValidationException("Platform ID is required for an update.");
        }
        try {
            Platform platformToUpdate = platformDAO.findById(id);
            if (platformToUpdate == null) {
                throw new ValidationException("Platform with ID " + id + " not found.");
            }
            
            Platform existingPlatformWithNewName = platformDAO.findByName(name.trim());
            if (existingPlatformWithNewName != null && !existingPlatformWithNewName.getId().equals(id)) {
                throw new ValidationException("Another platform with the name '" + name + "' already exists.");
            }

            platformToUpdate.setName(name.trim());
            platformToUpdate.setSymbolPath(symbolPath);
            return platformDAO.update(platformToUpdate);
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to update platform with ID " + id, e);
        }
    }

    /**
     * Deletes a platform by its ID.
     * @param id The ID of the platform to delete.
     * @throws ServiceException on database errors.
     */
    public void deleteById(Long id) throws ServiceException {
        try {
            platformDAO.delete(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Failed to delete platform by ID: " + id, e);
        }
    }
    // #endregion

    // #region Finder Methods
    
    public Platform findById(Long id) throws ServiceException {
        try {
            return platformDAO.findById(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding platform by ID: " + id, e);
        }
    }

    public Platform findByName(String name) throws ServiceException {
        try {
            return platformDAO.findByName(name);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding platform by name: " + name, e);
        }
    }
    
    public List<Platform> findAll() throws ServiceException {
        try {
            return platformDAO.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding all platforms.", e);
        }
    }

    public List<Platform> findByNameContaining(String searchTerm) throws ServiceException {
        try {
            return platformDAO.findByNameContaining(searchTerm);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding platforms by search term: " + searchTerm, e);
        }
    }

    /**
     * Finds a platform by name. If it doesn't exist, a new one is created.
     * @param name The name of the platform.
     * @param symbolPath The path to the symbol for the new platform, if created.
     * @return The existing or newly created Platform entity.
     * @throws ValidationException if the name is null or empty.
     * @throws ServiceException on database errors.
     */
    public Platform createOrFind(String name, String symbolPath) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Platform name cannot be empty.");
        }
        try {
            Platform existing = findByName(name);
            return existing != null ? existing : createPlatform(name, symbolPath);
        } catch (ServiceException e) {
            throw e;
        }
    }
    // #endregion
}