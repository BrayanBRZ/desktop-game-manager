package service;

import dao.GameDAO;
import dao.UserDAO;
import model.Game;
import model.User;
import model.UserGame;

import java.time.LocalDate;
import java.util.List;

public class UserService extends BaseService {

    // #region Validation
    private void validateUserData(String name, String password) throws ValidationException {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("O nome de usuário não pode estar vazio.");
        if (password == null || password.isEmpty())
            throw new ValidationException("A senha não pode estar vazia.");
    }

    /**
     * Verifica se o usuário já possui o jogo na biblioteca.
     */
    private boolean userHasGame(User user, Game game) {
        return user.getUserGames().stream()
                .anyMatch(ug -> ug.getGame().getId().equals(game.getId()));
    }
    // #endregion

    // #region CRUD and Library Management
    public User registerUser(String name, String password)
            throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);

            validateUserData(name, password);
            if (userDAO.findByName(name.trim()) != null)
                throw new ValidationException("O nome de usuário '" + name + "' já está em uso.");

            User user = new User(name.trim(), password);
            userDAO.save(user);
            return user;
        });
    }

    public User updateUserProfile(Long id, String name, LocalDate birthDate, String avatarPath)
            throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);

            if (id == null)
                throw new ValidationException("O ID do usuário é obrigatório.");
            if (name == null || name.trim().isEmpty())
                throw new ValidationException("O nome de usuário não pode estar vazio.");

            User existing = userDAO.findById(id);
            if (existing == null)
                throw new ValidationException("Usuário com ID " + id + " não encontrado.");

            User duplicate = userDAO.findByName(name.trim());
            if (duplicate != null && !duplicate.getId().equals(id))
                throw new ValidationException("O nome de usuário '" + name + "' já está em uso.");

            existing.setName(name.trim());
            existing.setBirthDate(birthDate);
            existing.setAvatarPath(avatarPath);

            return userDAO.update(existing);
        });
    }

    public void deleteUser(Long id) throws ServiceException {
        executeInTransaction(em -> {
            new UserDAO(em).delete(id);
            return null;
        });
    }

    /**
     * Adiciona um jogo à biblioteca do usuário, se ainda não existir.
     */
    public void addGameToLibrary(Long userId, Long gameId)
            throws ServiceException, ValidationException {
        executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);
            GameDAO gameDAO = new GameDAO(em);

            User user = userDAO.findById(userId);
            if (user == null)
                throw new ValidationException("Usuário com ID " + userId + " não encontrado.");

            Game game = gameDAO.findById(gameId);
            if (game == null)
                throw new ValidationException("Jogo com ID " + gameId + " não encontrado.");

            if (userHasGame(user, game))
                throw new ValidationException("O usuário já possui o jogo '" + game.getName() + "' na biblioteca.");

            UserGame userGame = new UserGame(user, game);
            user.getUserGames().add(userGame);

            userDAO.update(user);
            return null;
        });
    }

    /**
     * Remove um jogo da biblioteca do usuário, se existir.
     */
    public void removeGameFromLibrary(Long userId, Long gameId)
            throws ServiceException, ValidationException {
        executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);
            GameDAO gameDAO = new GameDAO(em);

            User user = userDAO.findById(userId);
            if (user == null)
                throw new ValidationException("Usuário com ID " + userId + " não encontrado.");

            Game game = gameDAO.findById(gameId);
            if (game == null)
                throw new ValidationException("Jogo com ID " + gameId + " não encontrado.");

            UserGame userGame = user.getUserGames().stream()
                    .filter(ug -> ug.getGame().getId().equals(game.getId()))
                    .findFirst()
                    .orElse(null);

            if (userGame == null)
                throw new ValidationException("O usuário não possui o jogo '" + game.getName() + "' na biblioteca.");

            user.getUserGames().remove(userGame);
            userDAO.update(user);
            return null;
        });
    }
    // #endregion

    // #region Read-Only Operations
    public User findById(Long id) throws ServiceException {
        return executeReadOnly(em -> new UserDAO(em).findById(id));
    }

    public User findByName(String name) throws ServiceException {
        return executeReadOnly(em -> new UserDAO(em).findByName(name));
    }

    public List<User> findAll() throws ServiceException {
        return executeReadOnly(em -> new UserDAO(em).findAll());
    }

    public List<User> findByNameContaining(String term) throws ServiceException {
        return executeReadOnly(em -> new UserDAO(em).findByNameContaining(term));
    }

    public List<User> findByBirthDate(LocalDate date) throws ServiceException {
        return executeReadOnly(em -> new UserDAO(em).findByBirthDate(date));
    }

    public List<User> findByAge(int age) throws ServiceException {
        return executeReadOnly(em -> new UserDAO(em).findByAge(age));
    }

    public List<User> findByGameId(Long gameId) throws ServiceException {
        return executeReadOnly(em -> new UserDAO(em).findByGameId(gameId));
    }

    public List<User> findByGameName(String gameName) throws ServiceException {
        return executeReadOnly(em -> new UserDAO(em).findByGameName(gameName));
    }
    // #endregion
}
