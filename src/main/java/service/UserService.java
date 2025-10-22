package service;

import dao.GameDAO;
import dao.UserDAO;
import model.Game;
import model.User;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.PersistenceException;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final GameDAO gameDAO = new GameDAO(); // Necessário para buscar jogos

    // #region Private Validation Logic
    private void validateUserData(String username, String password) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password cannot be empty.");
        }
    }
    // #endregion

    // #region CRUD and Core Operations
    /**
     * Registers a new user.
     *
     * @param username The user's name.
     * @param password The user's password.
     * @return The newly created User.
     * @throws ValidationException if username is taken or data is invalid.
     * @throws ServiceException on database errors.
     */
    public User registerUser(String username, String password) throws ValidationException, ServiceException {
        validateUserData(username, password);
        try {
            if (userDAO.findByUsername(username.trim()) != null) {
                throw new ValidationException("Username '" + username + "' is already taken.");
            }
            // Na prática, você deve aplicar hash na senha aqui antes de salvar!
            User newUser = new User(username.trim(), password);
            userDAO.save(newUser);
            return newUser;
        } catch (PersistenceException e) {
            throw new ServiceException("Could not register user.", e);
        }
    }

    /**
     * Updates a user's profile information.
     *
     * @param userId The ID of the user to update.
     * @param newUsername The new username.
     * @param newBirthDate The new birth date.
     * @param newAvatarPath The new avatar path.
     * @return The updated User entity.
     * @throws ValidationException if data is invalid or user not found.
     * @throws ServiceException on database errors.
     */
    public User updateUserProfile(Long userId, String newUsername, LocalDate newBirthDate, String newAvatarPath)
            throws ValidationException, ServiceException {
        if (userId == null) {
            throw new ValidationException("User ID is required for an update.");
        }
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty.");
        }
        try {
            User userToUpdate = userDAO.findById(userId);
            if (userToUpdate == null) {
                throw new ValidationException("User with ID " + userId + " not found.");
            }

            // Verifica se o novo username já está em uso por OUTRO usuário
            User existingUser = userDAO.findByUsername(newUsername.trim());
            if (existingUser != null && !existingUser.getId().equals(userId)) {
                throw new ValidationException("Username '" + newUsername + "' is already taken.");
            }

            userToUpdate.setUsername(newUsername.trim());
            userToUpdate.setBirthDate(newBirthDate);
            userToUpdate.setAvatarPath(newAvatarPath);

            return userDAO.update(userToUpdate);
        } catch (PersistenceException e) {
            throw new ServiceException("Could not update user profile.", e);
        }
    }

    /**
     * Adds a game to a user's library.
     *
     * @param userId The ID of the user.
     * @param gameId The ID of the game to add.
     * @throws ValidationException if user or game is not found.
     * @throws ServiceException on database errors.
     */
    public void addGameToLibrary(Long userId, Long gameId) throws ValidationException, ServiceException {
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new ValidationException("User with ID " + userId + " not found.");
            }
            Game game = gameDAO.findById(gameId);
            if (game == null) {
                throw new ValidationException("Game with ID " + gameId + " not found.");
            }

            user.addGame(game);
            userDAO.update(user);

        } catch (PersistenceException e) {
            throw new ServiceException("Could not add game to library.", e);
        }
    }

    /**
     * Removes a game from a user's library.
     *
     * @param userId The ID of the user.
     * @param gameId The ID of the game to remove.
     * @throws ValidationException if user or game is not found.
     * @throws ServiceException on database errors.
     */
    public void removeGameFromLibrary(Long userId, Long gameId) throws ValidationException, ServiceException {
        try {
            User user = userDAO.findById(userId);
            if (user == null) {
                throw new ValidationException("User with ID " + userId + " not found.");
            }
            Game game = gameDAO.findById(gameId);
            if (game == null) {
                throw new ValidationException("Game with ID " + gameId + " not found.");
            }

            user.removeGame(game);
            userDAO.update(user);

        } catch (PersistenceException e) {
            throw new ServiceException("Could not remove game from library.", e);
        }
    }

    public void deleteById(Long id) throws ServiceException {
        try {
            userDAO.delete(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error deleting user by ID: " + id, e);
        }
    }
    // #endregion

    // #region Finder Methods (Exposing all UserDAO methods)
    /**
     * Finds a user by their unique ID.
     *
     * @param id The user's ID.
     * @return The found User entity, or null if not found.
     * @throws ServiceException on database errors.
     */
    public User findById(Long id) throws ServiceException {
        try {
            return userDAO.findById(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding user by ID: " + id, e);
        }
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all users.
     * @throws ServiceException on database errors.
     */
    public List<User> findAll() throws ServiceException {
        try {
            return userDAO.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding all users.", e);
        }
    }

    /**
     * Finds a single User entity by its exact username.
     *
     * @param username The username to search for.
     * @return The found User entity, or null if not found.
     * @throws ServiceException on database errors.
     */
    public User findByUsername(String username) throws ServiceException {
        try {
            return userDAO.findByUsername(username);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding user by username: " + username, e);
        }
    }

    /**
     * Finds a list of users whose usernames contain the given search term.
     *
     * @param searchTerm The text to search for within usernames.
     * @return A list of matching users.
     * @throws ServiceException on database errors.
     */
    public List<User> findByNameContaining(String searchTerm) throws ServiceException {
        try {
            return userDAO.findByNameContaining(searchTerm);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding users by search term: " + searchTerm, e);
        }
    }

    /**
     * Finds all users born on a specific date.
     *
     * @param birthDate The exact birth date to search for.
     * @return A list of users born on that date.
     * @throws ServiceException on database errors.
     */
    public List<User> findByBirthDate(LocalDate birthDate) throws ServiceException {
        try {
            return userDAO.findByBirthDate(birthDate);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding users by birth date: " + birthDate, e);
        }
    }

    /**
     * Finds all users who are currently a specific age.
     *
     * @param age The age to search for.
     * @return A list of users with that specific age.
     * @throws ServiceException on database errors.
     */
    public List<User> findByAge(int age) throws ServiceException {
        try {
            return userDAO.findByAge(age);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding users by age: " + age, e);
        }
    }

    /**
     * Finds all users who have a specific game in their library, by the game's
     * ID.
     *
     * @param gameId The ID of the game to search for.
     * @return A list of users who own the game.
     * @throws ServiceException on database errors.
     */
    public List<User> findByGameId(Long gameId) throws ServiceException {
        try {
            return userDAO.findByGameId(gameId);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding users by game ID: " + gameId, e);
        }
    }

    /**
     * Finds all users who have a specific game in their library, by the game's
     * name.
     *
     * @param gameName The name of the game to search for.
     * @return A list of users who own the game.
     * @throws ServiceException on database errors.
     */
    public List<User> findByGameName(String gameName) throws ServiceException {
        try {
            return userDAO.findByGameName(gameName);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding users by game name: " + gameName, e);
        }
    }
    // #endregion
}
