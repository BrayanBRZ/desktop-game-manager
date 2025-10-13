package service;

import dao.GameDAO;
import dao.UserDAO;
import model.Game;
import model.User;
import javax.persistence.PersistenceException;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final GameDAO gameDAO = new GameDAO();

    /**
     * Registers a new user.
     * @param username The user's name.
     * @param password The user's password.
     * @return The newly created User.
     * @throws ValidationException if username is taken or data is invalid.
     * @throws ServiceException on database errors.
     */
    public User registerUser(String username, String password) throws ValidationException, ServiceException {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new ValidationException("Username and password cannot be empty.");
        }
        try {
            if (userDAO.findByUsername(username) != null) {
                throw new ValidationException("Username '" + username + "' is already taken.");
            }
            // Na prática, você deve aplicar hash na senha aqui antes de salvar!
            User newUser = new User(username, password);
            userDAO.save(newUser);
            return newUser;
        } catch (PersistenceException e) {
            throw new ServiceException("Could not register user.", e);
        }
    }
    
    /**
     * Adds a game to a user's library.
     * @param userId The ID of the user.
     * @param gameId The ID of the game to add.
     * @throws ValidationException if user or game is not found, or game is already in library.
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

            // Usando os métodos helper da sua entidade User
            user.addGame(game);
            
            // Persiste a mudança
            userDAO.update(user);

        } catch (PersistenceException e) {
            throw new ServiceException("Could not add game to library.", e);
        }
    }
    
    /**
     * Removes a game from a user's library.
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
    
    public User findById(Long id) throws ServiceException {
        try {
            return userDAO.findById(id);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding user by ID: " + id, e);
        }
    }
}