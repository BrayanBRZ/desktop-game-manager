package service;

import dao.DeveloperDAO;
import model.Developer;

import java.util.List;

public class DeveloperService extends BaseService {

    // #region CRUD Operations
    public Developer createDeveloper(String name, String location, String symbolPath)
            throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            DeveloperDAO developerDAO = new DeveloperDAO(em);

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
        });
    }

    public Developer updateDeveloper(Long id, String name, String location, String symbolPath)
            throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            DeveloperDAO developerDAO = new DeveloperDAO(em);

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
            existing.setLocation(location);
            existing.setSymbolPath(symbolPath);

            return developerDAO.update(existing);
        });
    }

    public void deleteDeveloper(Long id) throws ServiceException {
        executeInTransaction(em -> {
            new DeveloperDAO(em).delete(id);
        });
    }
    // #endregion

    // #region Read-Only Operations
    public Developer findById(Long id) throws ServiceException {
        return executeReadOnly(em -> new DeveloperDAO(em).findById(id));
    }

    public Developer findByName(String name) throws ServiceException {
        return executeReadOnly(em -> new DeveloperDAO(em).findByName(name));
    }

    public List<Developer> findAll() throws ServiceException {
        return executeReadOnly(em -> new DeveloperDAO(em).findAll());
    }

    public List<Developer> findByNameContaining(String term) throws ServiceException {
        return executeReadOnly(em -> new DeveloperDAO(em).findByNameContaining(term));
    }

    public Developer createOrFind(String name) throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            DeveloperDAO dao = new DeveloperDAO(em);
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Nome do desenvolvedor não pode estar vazio.");
            }

            Developer existing = dao.findByName(name.trim());
            if (existing != null) {
                return existing;
            }

            Developer newDev = new Developer();
            newDev.setName(name.trim());
            dao.save(newDev);
            return newDev;
        });
    }
    // #endregion
}
