package service.game;

import model.game.Developer;
import dao.game.DeveloperDAO;
import service.exception.ServiceException;
import service.exception.ValidationException;
import utils.MyLinkedList;

public class DeveloperService {

    private final DeveloperDAO developerDAO;

    public DeveloperService() {
        this.developerDAO = new DeveloperDAO();
    }

    // #region CRUD Operations
    public Developer createDeveloper(String name)
            throws ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do desenvolvedor não pode estar vazio.");
        }
        if (developerDAO.findByName(name.trim()) != null) {
            throw new ValidationException("Já existe um desenvolvedor com o nome '" + name + "'.");
        }

        Developer newDeveloper = new Developer();
        newDeveloper.setName(name.trim());

        developerDAO.save(newDeveloper);
        return newDeveloper;
    }

    public Developer updateDeveloper(Long id, String name)
            throws ValidationException {

        if (id == null) {
            throw new ValidationException("ID do desenvolvedor é obrigatório.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do desenvolvedor não pode estar vazio.");
        }

        Developer existing = developerDAO.findById(id);
        if (existing == null) {
            throw new ValidationException("Desenvolvedor com ID " + id + " não encontrado.");
        }
        Developer duplicate = developerDAO.findByName(name.trim());
        if (duplicate != null && !duplicate.getId().equals(id)) {
            throw new ValidationException("Já existe outro desenvolvedor com o nome '" + name + "'.");
        }

        existing.setName(name.trim());

        return developerDAO.update(existing);
    }

    public Developer createOrFind(String name)
            throws ServiceException, ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do desenvolvedor não pode estar vazio.");
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

    public void deleteDeveloper(Long id) {
        developerDAO.delete(id);
    }
    // #endregion CRUD Operations

    // #region Read-Only Operations
    public Developer findById(Long id) {
        return developerDAO.findById(id);
    }

    public Developer findByName(String id) {
        return developerDAO.findByName(id);
    }

    public MyLinkedList<Developer> findAll() {
        return developerDAO.findAll();
    }
    // #endregion Read-Only Operations
}
