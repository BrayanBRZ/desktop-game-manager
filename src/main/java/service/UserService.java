package service;

import dao.GameDAO;
import dao.UserDAO;
import model.Game;
import model.User;
import model.UserGame;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço responsável por operações relacionadas a usuários: - Registro, login,
 * atualização de perfil, senha e biblioteca de jogos. - Retorna sempre usuários
 * sem o campo de senha por segurança.
 */
public class UserService extends BaseService {

    // #region Helper Methods
    private void validateBasicUserData(String name, String password) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("O nome de usuário não pode estar vazio.");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException("A senha não pode estar vazia.");
        }
        if (name.trim().length() < 3) {
            throw new ValidationException("O nome de usuário deve ter ao menos 3 caracteres.");
        }
        if (password.length() < 6) {
            throw new ValidationException("A senha deve ter ao menos 6 caracteres.");
        }
    }

    private void validateProfileUpdateData(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("O nome de usuário não pode estar vazio.");
        }
        if (name.trim().length() < 3) {
            throw new ValidationException("O nome de usuário deve ter ao menos 3 caracteres.");
        }
    }

    private String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes());
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao inicializar algoritmo de hash", e);
        }
    }

    private boolean passwordMatches(String plain, String hashed) {
        return hashed != null && Objects.equals(hashPassword(plain), hashed);
    }

    /**
     * Retorna uma cópia do usuário sem senha.
     */
    private User sanitizeUser(User user) {
        if (user == null) {
            return null;
        }
        User safe = new User();
        safe.setId(user.getId());
        safe.setName(user.getName());
        safe.setAvatarPath(user.getAvatarPath());
        safe.setBirthDate(user.getBirthDate());
        safe.setUserGames(user.getUserGames());
        safe.setCreatedAt(user.getCreatedAt());
        safe.setUpdatedAt(user.getUpdatedAt());
        return safe;
    }

    /**
     * Retorna uma lista de usuários sanitizados (sem senha).
     */
    private List<User> sanitizeList(List<User> users) {
        return users.stream().map(this::sanitizeUser).collect(Collectors.toList());
    }

    // #endregion Helper Methods
    // #region Register and Login
    public User registerUser(String name, String password) throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);
            validateBasicUserData(name, password);

            String trimmed = name.trim();
            if (userDAO.findByName(trimmed) != null) {
                throw new ValidationException("O nome de usuário '" + trimmed + "' já está em uso.");
            }

            User user = new User(trimmed, hashPassword(password));
            userDAO.save(user);
            return sanitizeUser(user);
        });
    }

    /**
     * Autentica um usuário por nome e senha. Retorna o usuário sem senha.
     */
    public User login(String name, String password) throws ServiceException {
        return executeReadOnly(em -> {
            if (name == null || password == null) {
                return null;
            }
            UserDAO userDAO = new UserDAO(em);
            User u = userDAO.findByName(name.trim());
            if (u == null || !passwordMatches(password, u.getPassword())) {
                return null;
            }
            return sanitizeUser(u);
        });
    }

    public void changePassword(Long userId, String currentPassword, String newPassword)
            throws ServiceException, ValidationException {
        executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);

            if (userId == null) {
                throw new ValidationException("ID do usuário é obrigatório.");
            }
            if (currentPassword == null || currentPassword.isEmpty()) {
                throw new ValidationException("Senha atual é obrigatória.");
            }
            if (newPassword == null || newPassword.length() < 6) {
                throw new ValidationException("Nova senha inválida (mínimo 6 caracteres).");
            }

            User existing = userDAO.findById(userId);
            if (existing == null) {
                throw new ValidationException("Usuário com ID " + userId + " não encontrado.");
            }
            if (!passwordMatches(currentPassword, existing.getPassword())) {
                throw new ValidationException("Senha atual incorreta.");
            }

            existing.setPassword(hashPassword(newPassword));
            userDAO.update(existing);
            return null;
        });
    }

    public void resetPassword(Long userId, String newPassword) throws ServiceException, ValidationException {
        executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);
            if (userId == null) {
                throw new ValidationException("ID do usuário é obrigatório.");
            }
            if (newPassword == null || newPassword.length() < 6) {
                throw new ValidationException("Nova senha inválida (mínimo 6 caracteres).");
            }

            User existing = userDAO.findById(userId);
            if (existing == null) {
                throw new ValidationException("Usuário com ID " + userId + " não encontrado.");
            }

            existing.setPassword(hashPassword(newPassword));
            userDAO.update(existing);
            return null;
        });
    }

    // #endregion Register and Login
    // #region Profile and Library Management
    public User updateUserProfile(Long id, String name, LocalDate birthDate, String avatarPath)
            throws ServiceException, ValidationException {
        return executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);
            if (id == null) {
                throw new ValidationException("O ID do usuário é obrigatório.");
            }
            validateProfileUpdateData(name);

            User existing = userDAO.findById(id);
            if (existing == null) {
                throw new ValidationException("Usuário com ID " + id + " não encontrado.");
            }

            User duplicate = userDAO.findByName(name.trim());
            if (duplicate != null && !duplicate.getId().equals(id)) {
                throw new ValidationException("O nome de usuário '" + name.trim() + "' já está em uso.");
            }

            existing.setName(name.trim());
            existing.setBirthDate(birthDate);
            existing.setAvatarPath(avatarPath);

            return sanitizeUser(userDAO.update(existing));
        });
    }

    public void deleteUser(Long id) throws ServiceException {
        executeInTransaction(em -> {
            new UserDAO(em).delete(id);
            return null;
        });
    }

    public void addGameToLibrary(Long userId, Long gameId)
            throws ServiceException, ValidationException {
        executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);
            GameDAO gameDAO = new GameDAO(em);

            User user = userDAO.findById(userId);
            if (user == null) {
                throw new ValidationException("Usuário com ID " + userId + " não encontrado.");
            }

            Game game = gameDAO.findById(gameId);
            if (game == null) {
                throw new ValidationException("Jogo com ID " + gameId + " não encontrado.");
            }

            boolean has = user.getUserGames().stream()
                    .anyMatch(ug -> ug.getGame().getId().equals(game.getId()));
            if (has) {
                throw new ValidationException("O usuário já possui o jogo '" + game.getName() + "'.");
            }

            user.getUserGames().add(new UserGame(user, game));
            userDAO.update(user);
            return null;
        });
    }

    public void removeGameFromLibrary(Long userId, Long gameId)
            throws ServiceException, ValidationException {
        executeInTransaction(em -> {
            UserDAO userDAO = new UserDAO(em);
            GameDAO gameDAO = new GameDAO(em);

            User user = userDAO.findById(userId);
            if (user == null) {
                throw new ValidationException("Usuário com ID " + userId + " não encontrado.");
            }

            Game game = gameDAO.findById(gameId);
            if (game == null) {
                throw new ValidationException("Jogo com ID " + gameId + " não encontrado.");
            }

            UserGame found = user.getUserGames().stream()
                    .filter(ug -> ug.getGame().getId().equals(game.getId()))
                    .findFirst()
                    .orElse(null);

            if (found == null) {
                throw new ValidationException("O usuário não possui o jogo '" + game.getName() + "'.");
            }

            user.getUserGames().remove(found);
            userDAO.update(user);
            return null;
        });
    }

    // #endregion Profile and Library Management
    // #region Read-Only Operations
    public User findById(Long id) throws ServiceException {
        return executeReadOnly(em -> sanitizeUser(new UserDAO(em).findById(id)));
    }

    public User findByName(String name) throws ServiceException {
        return executeReadOnly(em -> sanitizeUser(new UserDAO(em).findByName(name)));
    }

    public List<User> findAll() throws ServiceException {
        return executeReadOnly(em -> sanitizeList(new UserDAO(em).findAll()));
    }

    public List<User> findByNameContaining(String term) throws ServiceException {
        return executeReadOnly(em -> sanitizeList(new UserDAO(em).findByNameContaining(term)));
    }

    public List<User> findByBirthDate(LocalDate date) throws ServiceException {
        return executeReadOnly(em -> sanitizeList(new UserDAO(em).findByBirthDate(date)));
    }

    public List<User> findByAge(int age) throws ServiceException {
        return executeReadOnly(em -> sanitizeList(new UserDAO(em).findByAge(age)));
    }

    public List<User> findByGameId(Long gameId) throws ServiceException {
        return executeReadOnly(em -> sanitizeList(new UserDAO(em).findByGameId(gameId)));
    }

    public List<User> findByGameName(String gameName) throws ServiceException {
        return executeReadOnly(em -> sanitizeList(new UserDAO(em).findByGameName(gameName)));
    }
    // #endregion Read-Only Operations
}
