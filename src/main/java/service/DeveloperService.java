package service;

import java.util.List;

import dao.DeveloperDAO;
import model.Developer;

public class DeveloperService {

    private final DeveloperDAO developerDAO;

    public DeveloperService() {
        this.developerDAO = new DeveloperDAO();
    }

    // #region CRUD Operations
    public Developer createDeveloper(String name, String location, String symbolPath)
            throws ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do desenvolvedor não pode estar vazio.");
        }
        if (developerDAO.findByName(name.trim()) != null) {
            throw new ValidationException("Já existe um desenvolvedor com o nome '" + name + "'.");
        }

        Developer newDeveloper = new Developer();
        newDeveloper.setName(name.trim());
        newDeveloper.setLocation(location);
        newDeveloper.setSymbolPath(symbolPath);

        developerDAO.save(newDeveloper);
        return newDeveloper;
    }

    public Developer updateDeveloper(Long id, String name, String location, String symbolPath)
            throws ValidationException {

        if (id == null) {
            throw new ValidationException("ID do desenvolvedor é obrigatório.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do desenvolvedor não pode estar vazio.");
        }

        // Busca e validação DENTRO da transação.
        Developer existing = developerDAO.findById(id);
        if (existing == null) {
            throw new ValidationException("Desenvolvedor com ID " + id + " não encontrado.");
        }
        Developer duplicate = developerDAO.findByName(name.trim());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new ValidationException("Já existe outro desenvolvedor com o nome '" + name + "'.");
        }

        existing.setName(name.trim());
        existing.setLocation(location);
        existing.setSymbolPath(symbolPath);

        return developerDAO.update(existing);
    }

    // #region Create or Find
    public Developer createOrFind(String name)
            throws ServiceException, ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do gênero não pode estar vazio.");
        }

        Developer existing = developerDAO.findByName(name.trim());
        if (existing != null) {
            return existing;
        }

        Developer newDev = new Developer();
        newDev.setName(name.trim());
        developerDAO.save(newDev);
        return newDev;
    }
    // #endregion Create or Find

    public void deleteDeveloper(Long id) {
        developerDAO.delete(id);
    }
    // #endregion CRUD Operations

    // #region Read-Only Operations
    public Developer findById(Long id) {
        return developerDAO.findById(id);
    }

    public List<Developer> findAll() {
        return developerDAO.findAll();
    }
    // #endregion Read-Only Operations
}
