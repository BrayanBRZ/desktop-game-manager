package service;

import dao.GenreDAO;
import model.Genre;

import java.util.List;

public class GenreService {

    private final GenreDAO genreDAO;

    public GenreService() {
        this.genreDAO = new GenreDAO();
    }

    // #region CRUD Operations
    public Genre createGenre(String name) throws ServiceException, ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do gênero não pode estar vazio.");
        }

        if (genreDAO.findByName(name.trim()) != null) {
            throw new ValidationException("Já existe um gênero com o nome '" + name + "'.");
        }

        Genre newGenre = new Genre(name.trim());
        genreDAO.save(newGenre);
        return newGenre;
    }

    public Genre updateGenre(Long id, String name) throws ServiceException, ValidationException {

        if (id == null) {
            throw new ValidationException("ID do gênero é obrigatório.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do gênero não pode estar vazio.");
        }

        Genre existing = genreDAO.findById(id);
        if (existing == null) {
            throw new ValidationException("Gênero com ID " + id + " não encontrado.");
        }

        Genre duplicate = genreDAO.findByName(name.trim());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new ValidationException("Já existe outro gênero com o nome '" + name + "'.");
        }

        existing.setName(name.trim());
        return genreDAO.update(existing);
    }

    public void deleteGenre(Long id) throws ServiceException {
        genreDAO.delete(id);
    }
    // #endregion CRUD Operations

    // #region Create or Find
    public Genre createOrFind(String name)
            throws ServiceException, ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do gênero não pode estar vazio.");
        }

        Genre existing = genreDAO.findByName(name.trim());
        if (existing != null) {
            return existing;
        }

        Genre newDev = new Genre();
        newDev.setName(name.trim());
        genreDAO.save(newDev);
        return newDev;
    }
    // #endregion Create or Find

    // #region Read-Only Operations
    public Genre findById(Long id) throws ServiceException {
        return genreDAO.executeReadOnly(em -> genreDAO.findById(id));
    }

    public Genre findByName(String name) throws ServiceException {
        return genreDAO.executeReadOnly(em -> genreDAO.findByName(name));
    }

    public List<Genre> findAll() throws ServiceException {
        return genreDAO.executeReadOnly(em -> genreDAO.findAll());
    }

    public List<Genre> findByNameContaining(String term) throws ServiceException {
        return genreDAO.executeReadOnly(em -> genreDAO.findByNameContaining(term));
    }
    // #endregion Read-Only Operations
}
