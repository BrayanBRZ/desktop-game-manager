package service.game;

import model.game.Platform;
import dao.game.PlatformDAO;
import service.exception.ServiceException;
import service.exception.ValidationException;
import utils.MyLinkedList;

public class PlatformService {

    private final PlatformDAO platformDAO;

    public PlatformService() {
        this.platformDAO = new PlatformDAO();
    }

    // #region CRUD Operations
    public Platform createPlatform(String name)
            throws ServiceException, ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome da plataforma não pode estar vazio.");
        }

        if (platformDAO.findByName(name.trim()) != null) {
            throw new ValidationException("Já existe uma plataforma com o nome '" + name + "'.");
        }

        Platform platform = new Platform(name.trim());
        platformDAO.save(platform);
        return platform;

    }

    public Platform updatePlatform(Long id, String name)
            throws ServiceException, ValidationException {

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
        return platformDAO.update(existing);

    }

    public void deletePlatform(Long id) throws ServiceException {
        platformDAO.delete(id);
    }
    // #endregion CRUD Operations

    // #region Create or Find
    public Platform createOrFind(String name)
            throws ServiceException, ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome da plataforma não pode estar vazio.");
        }

        Platform existing = platformDAO.findByName(name.trim());
        if (existing != null) {
            return existing;
        }

        Platform platform = new Platform(name.trim());
        platformDAO.save(platform);
        return platform;

    }
    // #endregion Create or Find

    // #region Read-Only Operations
    public Platform findById(Long id) throws ServiceException {
        return platformDAO.findById(id);
    }

    public Platform findByName(String name) throws ServiceException {
        return platformDAO.findByName(name);
    }

    public MyLinkedList<Platform> findAll() throws ServiceException {
        return platformDAO.findAll();
    }

    public MyLinkedList<Platform> findByNameContaining(String term) throws ServiceException {
        return platformDAO.findByNameContaining(term);
    }
    // #endregion Read-Only Operations
}
