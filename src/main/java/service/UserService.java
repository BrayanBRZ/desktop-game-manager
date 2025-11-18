package service;

import dao.GameDAO;
import dao.UserDAO;
import model.game.Game;
import model.user.User;
import model.user.UserGame;
import service.exception.ServiceException;
import service.exception.ValidationException;

import java.time.LocalDate;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final GameDAO gameDAO = new GameDAO();

    // === Perfil ===
    public User updateUserProfile(Long id, String name, LocalDate birthDate, String avatarPath)
            throws ValidationException, ServiceException {

        validateProfileUpdateData(name);
        User user = userDAO.findById(id);
        if (user == null) throw new ValidationException("Usuário não encontrado.");

        if (!user.getName().equals(name.trim())) {
            if (userDAO.findByName(name.trim()) != null) {
                throw new ValidationException("Nome já em uso.");
            }
        }

        user.setName(name.trim());
        user.setBirthDate(birthDate);
        user.setAvatarPath(avatarPath);
        return userDAO.update(user);
    }

    // === Biblioteca ===
    public void addGameToLibrary(Long userId, Long gameId) throws ValidationException {
        User user = userDAO.findById(userId);
        Game game = gameDAO.findById(gameId);
        if (user == null || game == null) throw new ValidationException("Usuário ou jogo não encontrado.");

        if (user.getUserGames().stream().anyMatch(ug -> ug.getGame().getId().equals(gameId))) {
            throw new ValidationException("Você já possui este jogo.");
        }

        user.getUserGames().add(new UserGame(user, game));
        userDAO.update(user);
    }

    public void removeGameFromLibrary(Long userId, Long gameId) throws ValidationException {
        User user = userDAO.findById(userId);
        if (user == null) throw new ValidationException("Usuário não encontrado.");

        user.getUserGames().removeIf(ug -> ug.getGame().getId().equals(gameId));
        userDAO.update(user);
    }

    // === Buscas ===
    public User findById(Long id) throws ServiceException {
        return  userDAO.findById(id);
    }

    public User findByName(String name) throws ServiceException {
        return userDAO.findByName(name.trim());
    }

    public List<User> findAll() throws ServiceException {
        return userDAO.findAll();
    }

    private void validateProfileUpdateData(String name) throws ValidationException {
        if (name == null || name.trim().length() < 3) {
            throw new ValidationException("Nome deve ter pelo menos 3 caracteres.");
        }
    }
}