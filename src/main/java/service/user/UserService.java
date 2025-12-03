package service.user;

import model.game.Game;
import model.user.User;
import model.user.UserGame;
import dao.game.GameDAO;
import dao.user.UserDAO;
import service.exception.ServiceException;
import service.exception.ValidationException;
import static core.AppConfig.ADMIN_PASSWORD;
import utils.ConsoleUtils;
import utils.MyLinkedList;
import java.time.LocalDate;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final GameDAO gameDAO = new GameDAO();

    public User updateUserProfile(Long id, String name, LocalDate birthDate) {

        validateProfileUpdateData(name);
        User user = userDAO.findById(id);
        if (user == null) {
            throw new ValidationException("Usuário não encontrado.");
        }

        if (!user.getName().equals(name.trim())) {
            if (userDAO.findByName(name.trim()) != null) {
                throw new ValidationException("Nome já em uso.");
            }
        }

        user.setName(name.trim());
        user.setBirthDate(birthDate);
        return userDAO.update(user);
    }

    public void deleteUser(Long id) throws ServiceException {
        User user = userDAO.findById(id);
        if (user == null) {
            throw new ServiceException("Usuário não encontrado.");
        }
        
        userDAO.delete(id);
    }

    public void addGameToLibrary(Long userId, String gameName) {
        User user = userDAO.findById(userId);
        Game game = gameDAO.findByName(gameName);
        if (user == null || game == null) {
            throw new ValidationException("Usuário ou jogo não encontrado.");
        }

        if (user.getUserGames().stream().anyMatch(ug -> ug.getGame().getId().equals(game.getId()))) {
            throw new ValidationException("Você já possui este jogo.");
        }

        user.getUserGames().add(new UserGame(user, game));
        userDAO.update(user);
    }

    public void removeGameFromLibrary(Long userId, Long gameId) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new ValidationException("Usuário não encontrado.");
        }

        user.getUserGames().removeIf(ug -> ug.getGame().getId().equals(gameId));
        userDAO.update(user);
    }

    public void passIsValid() {
        String password = ConsoleUtils.readString("Digite a senha MASTER para continuar: ", null);

        if (!password.equals(ADMIN_PASSWORD)) {
            throw new ValidationException("Senha incorreta. Operação cancelada.");
        }
    }
    
    // #region Read-Only Operations
    public User findById(Long id) throws ServiceException {
        return userDAO.findById(id);
    }

    public User findByName(String name) throws ServiceException {
        return userDAO.findByName(name.trim());
    }

    public MyLinkedList<User> findAll() throws ServiceException {
        return userDAO.findAll();
    }

    private void validateProfileUpdateData(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new ValidationException("Nome deve ter pelo menos 3 caracteres.");
        }
    }
    // #endregion Read-Only Operations
}
