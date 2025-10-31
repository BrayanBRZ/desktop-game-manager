package service;

import dao.PlatformDAO;
import model.Platform;

import java.util.List;

public class PlatformService extends BaseService {

    // #region CRUD Operations
    public Platform createPlatform(String name, String symbolPath)
            throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            PlatformDAO platformDAO = new PlatformDAO(em);

            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Nome da plataforma não pode estar vazio.");
            }

            if (platformDAO.findByName(name.trim()) != null) {
                throw new ValidationException("Já existe uma plataforma com o nome '" + name + "'.");
            }

            Platform platform = new Platform(name.trim());
            platform.setSymbolPath(symbolPath);
            platformDAO.save(platform);
            return platform;
        });
    }

    public Platform updatePlatform(Long id, String name, String symbolPath)
            throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            PlatformDAO platformDAO = new PlatformDAO(em);

            if (id == null) {
                throw new ValidationException("ID da plataforma é obrigatório.");
            }
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Nome da plataforma não pode estar vazio.");
            }

            Platform existing = platformDAO.findById(id);
            if (existing == null) {
                throw new ValidationException("Plataforma com ID " + id + " não encontrada.");
            }

            Platform duplicate = platformDAO.findByName(name.trim());
            if (duplicate != null && !duplicate.getId().equals(id)) {
                throw new ValidationException("Já existe outra plataforma com o nome '" + name + "'.");
            }

            existing.setName(name.trim());
            existing.setSymbolPath(symbolPath);
            return platformDAO.update(existing);
        });
    }

    public void deletePlatform(Long id) throws ServiceException {
        executeInTransaction(em -> {
            new PlatformDAO(em).delete(id);
            return null;
        });
    }

    public Platform createOrFind(String name)
            throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            PlatformDAO dao = new PlatformDAO(em);
            if (name == null || name.trim().isEmpty()) {
                throw new ValidationException("Nome da plataforma não pode estar vazio.");
            }

            Platform existing = dao.findByName(name.trim());
            if (existing != null) {
                return existing;
            }

            Platform platform = new Platform(name.trim());
            dao.save(platform);
            return platform;
        });
    }
    // #endregion CRUD Operations

    // #region Read-Only Operations
    public Platform findById(Long id) throws ServiceException {
        return executeReadOnly(em -> new PlatformDAO(em).findById(id));
    }

    public Platform findByName(String name) throws ServiceException {
        return executeReadOnly(em -> new PlatformDAO(em).findByName(name));
    }

    public List<Platform> findAll() throws ServiceException {
        return executeReadOnly(em -> new PlatformDAO(em).findAll());
    }

    public List<Platform> findByNameContaining(String term) throws ServiceException {
        return executeReadOnly(em -> new PlatformDAO(em).findByNameContaining(term));
    }
    // #endregion Read-Only Operations
}
