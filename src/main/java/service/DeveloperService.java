package service;

import dao.DeveloperDAO;
import model.Developer;
import javax.persistence.PersistenceException;
import java.util.List;

public class DeveloperService {

    private final DeveloperDAO developerDAO = new DeveloperDAO();

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
            Developer existingDeveloper = developerDAO.findByName(name);
            if (existingDeveloper != null) {
                return existingDeveloper;
            }
            Developer newDeveloper = new Developer(name.trim());
            developerDAO.save(newDeveloper);
            return newDeveloper;
        } catch (PersistenceException e) {
            throw new ServiceException("Error creating or finding developer: " + name, e);
        }
    }
    
    public Developer findById(Long id) throws ServiceException {
        try {
            return developerDAO.findById(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding developer by ID: " + id, e);
        }
    }

    public List<Developer> findAll() throws ServiceException {
        try {
            return developerDAO.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding all developers.", e);
        }
    }
    
    public void deleteById(Long id) throws ServiceException {
        try {
            developerDAO.delete(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error deleting developer by ID: " + id, e);
        }
    }
}