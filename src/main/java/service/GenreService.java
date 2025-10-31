package service;

import dao.GenreDAO;
import model.Genre;

import java.util.List;

public class GenreService extends BaseService {

    // #region CRUD Operations
    public Genre createGenre(String name) throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            GenreDAO genreDAO = new GenreDAO(em);

            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Nome do gênero não pode estar vazio.");
            }

            if (genreDAO.findByName(name.trim()) != null) {
                throw new ValidationException("Já existe um gênero com o nome '" + name + "'.");
            }

            Genre newGenre = new Genre(name.trim());
            genreDAO.save(newGenre);
            return newGenre;
        });
    }

    public Genre updateGenre(Long id, String name) throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            GenreDAO genreDAO = new GenreDAO(em);

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
        });
    }

    public void deleteGenre(Long id) throws ServiceException {
        executeInTransaction(em -> {
            new GenreDAO(em).delete(id);
        });
    }

    public Genre createOrFind(String name) throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            GenreDAO dao = new GenreDAO(em);
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Nome do gênero não pode estar vazio.");
            }

            Genre existing = dao.findByName(name.trim());
            if (existing != null) {
                return existing;
            }

            Genre newGenre = new Genre(name.trim());
            dao.save(newGenre);
            return newGenre;
        });
    }
    // #endregion CRUD Operations

    // #region Read-Only Operations
    public Genre findById(Long id) throws ServiceException {
        return executeReadOnly(em -> new GenreDAO(em).findById(id));
    }

    public Genre findByName(String name) throws ServiceException {
        return executeReadOnly(em -> new GenreDAO(em).findByName(name));
    }

    public List<Genre> findAll() throws ServiceException {
        return executeReadOnly(em -> new GenreDAO(em).findAll());
    }

    public List<Genre> findByNameContaining(String term) throws ServiceException {
        return executeReadOnly(em -> new GenreDAO(em).findByNameContaining(term));
    }
    // #endregion Read-Only Operations
}
