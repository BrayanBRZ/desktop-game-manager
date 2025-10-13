package service;

import dao.GenreDAO;
import model.Genre;
import javax.persistence.PersistenceException;
import java.util.List;

public class GenreService {

    private final GenreDAO genreDAO = new GenreDAO();

    /**
     * Finds a genre by name. If it doesn't exist, a new one is created.
     * This is useful for preventing duplicate genres.
     *
     * @param name The name of the genre.
     * @return The existing or newly created Genre entity.
     * @throws ValidationException if the name is null or empty.
     * @throws ServiceException if a database error occurs.
     */
    public Genre createOrFind(String name) throws ValidationException, ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Genre name cannot be empty.");
        }
        try {
            Genre existingGenre = genreDAO.findByName(name);
            if (existingGenre != null) {
                return existingGenre;
            }
            Genre newGenre = new Genre(name.trim());
            genreDAO.save(newGenre);
            return newGenre;
        } catch (PersistenceException e) {
            throw new ServiceException("Error creating or finding genre: " + name, e);
        }
    }
    
    public Genre findById(Long id) throws ServiceException {
        try {
            return genreDAO.findById(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding genre by ID: " + id, e);
        }
    }
    
    public List<Genre> findAll() throws ServiceException {
        try {
            return genreDAO.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding all genres.", e);
        }
    }

    public void deleteById(Long id) throws ServiceException {
        try {
            genreDAO.delete(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error deleting genre by ID: " + id, e);
        }
    }
}