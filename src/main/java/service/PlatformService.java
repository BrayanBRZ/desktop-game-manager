package service;

import dao.PlatformDAO;
import model.Platform;
import javax.persistence.PersistenceException;
import java.util.List;

public class PlatformService {

    private final PlatformDAO platformDAO = new PlatformDAO();

    /**
     * Finds a platform by name. If it doesn't exist, a new one is created.
     *
     * @param name The name of the platform.
     * @param symbolPath The path to the platform's symbol (can be null).
     * @return The existing or newly created Platform entity.
     * @throws ValidationException if the name is null or empty.
     * @throws ServiceException if a database error occurs.
     */
    public Platform createOrFind(String name, String symbolPath) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Platform name cannot be empty.");
        }
        try {
            Platform existingPlatform = platformDAO.findByName(name);
            if (existingPlatform != null) {
                return existingPlatform;
            }
            Platform newPlatform = new Platform(name.trim());
            newPlatform.setSymbolPath(symbolPath);
            platformDAO.save(newPlatform);
            return newPlatform;
        } catch (PersistenceException e) {
            throw new ServiceException("Error creating or finding platform: " + name, e);
        }
    }

    public Platform findById(Long id) throws ServiceException {
        try {
            return platformDAO.findById(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding platform by ID: " + id, e);
        }
    }

    public List<Platform> findAll() throws ServiceException {
        try {
            return platformDAO.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding all platforms.", e);
        }
    }
    
    public void deleteById(Long id) throws ServiceException {
        try {
            platformDAO.delete(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error deleting platform by ID: " + id, e);
        }
    }
}