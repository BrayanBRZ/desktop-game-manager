package service;

import dao.GameDAO;
import dao.UserDAO;
import model.Game;
import model.User;
import model.UserGame;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.gameDAO = new GameDAO();
    }

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

    // Returns a copy of the user without a password
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

    // Returns a list of sanitized users (without passwords)
    private List<User> sanitizeList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(this::sanitizeUser).collect(Collectors.toList());
    }

    // #endregion Helper Methods
    // #region Register and Login
    public User registerUser(String name, String password) throws ServiceException, ValidationException {

        validateBasicUserData(name, password);

        String trimmed = name.trim();
        if (userDAO.findByName(trimmed) != null) {
            throw new ValidationException("O nome de usuário '" + trimmed + "' já está em uso.");
        }

        User user = new User(trimmed, hashPassword(password));
        userDAO.save(user);
        return sanitizeUser(user);
    }

    /**
     * Autentica um usuário por nome e senha. Retorna o usuário sem senha.
     */
    public User login(String name, String password) throws ServiceException {

        if (name == null || password == null) {
            return null;
        }
        User u = userDAO.findByName(name.trim());
        if (u == null || !passwordMatches(password, u.getPassword())) {
            return null;
        }
        return sanitizeUser(u);
    }

    public void changePassword(Long userId, String currentPassword, String newPassword)
            throws ServiceException, ValidationException {

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
    }

    public void resetPassword(Long userId, String newPassword) throws ServiceException, ValidationException {
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
    }

    // #endregion Register and Login
    // #region Profile and Library Management
    public User updateUserProfile(Long id, String name, LocalDate birthDate, String avatarPath)
            throws ServiceException, ValidationException {

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
    }

    public void deleteUser(Long id) throws ServiceException {
        userDAO.delete(id);
    }

    public void addGameToLibrary(Long userId, Long gameId)
            throws ServiceException, ValidationException {

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
    }

    public void removeGameFromLibrary(Long userId, Long gameId)
            throws ServiceException, ValidationException {

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
    }
    // #endregion Profile and Library Management

    // #region Read-Only Operations
    // Generic Finders
    public User findById(Long id) throws ServiceException {
        return sanitizeUser(userDAO.findById(id));
    }

    public User findByName(String name) throws ServiceException {
        return sanitizeUser(userDAO.findByName(name));
    }

    public List<User> findAll() throws ServiceException {
        return sanitizeList(userDAO.findAll());
    }

    public List<User> findByNameContaining(String term) throws ServiceException {
        return sanitizeList(userDAO.findByNameContaining(term));
    }

    // Exclusive Finders
    public List<User> findByBirthDate(LocalDate date) throws ServiceException {
        return sanitizeList(userDAO.findByBirthDate(date));
    }

    public List<User> findByAge(int age) throws ServiceException {
        return sanitizeList(userDAO.findByAge(age));
    }

    public List<User> findByGameId(Long gameId) throws ServiceException {
        return sanitizeList(userDAO.findByGameId(gameId));
    }

    public List<User> findByGameName(String gameName) throws ServiceException {
        return sanitizeList(userDAO.findByGameName(gameName));
    }
    // #endregion Read-Only Operations
}
