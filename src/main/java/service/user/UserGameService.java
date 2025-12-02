package service.user;

import model.user.UserGame;
import model.user.UserGameState;
import dao.user.UserGameDAO;
import service.exception.ServiceException;
import service.exception.ValidationException;
import utils.MyLinkedList;
import java.time.LocalDateTime;

public class UserGameService {

    private final UserGameDAO userGameDAO;

    public UserGameService() {
        this.userGameDAO = new UserGameDAO();
    }

    // #region Data Update Methods
    public UserGame updateAllAttributes(Long userId, Long gameId,
            boolean estimated,
            UserGameState state,
            double totalTimePlayed,
            LocalDateTime lastPlayed)
            throws ServiceException {

            UserGame userGame = userGameDAO.findByUserAndGame(userId, gameId);
            if (userGame == null) {
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");
            }

            if (totalTimePlayed < 0) {
                throw new ValidationException("O tempo total jogado não pode ser negativo.");
            }

            userGame.setEstimated(estimated);
            userGame.setGameState(state);
            userGame.setTotaltimePlayed(totalTimePlayed);
            userGame.setLastTimePlayed(lastPlayed);

            return userGameDAO.update(userGame);
    }

    public UserGame updateEstimated(Long userId, Long gameId, boolean estimated) throws ServiceException {
        
            UserGame userGame = userGameDAO.findByUserAndGame(userId, gameId);
            if (userGame == null) {
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");
            }

            userGame.setEstimated(estimated);
            return userGameDAO.update(userGame);
    }

    public UserGame updateGameState(Long userId, Long gameId, UserGameState newState) throws ServiceException {
            UserGame userGame = userGameDAO.findByUserAndGame(userId, gameId);
            if (userGame == null) {
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");
            }

            userGame.setGameState(newState);
            return userGameDAO.update(userGame);
    }

    public UserGame updateTotalTimePlayed(Long userId, Long gameId, double newTime) throws ServiceException {
            UserGame userGame = userGameDAO.findByUserAndGame(userId, gameId);
            if (userGame == null) {
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");
            }

            if (newTime < 0) {
                throw new ValidationException("O tempo total jogado não pode ser negativo.");
            }

            userGame.setTotaltimePlayed(newTime);
            return userGameDAO.update(userGame);
    }

    public UserGame updateLastTimePlayed(Long userId, Long gameId, LocalDateTime dateTime) throws ServiceException {
            UserGame userGame = userGameDAO.findByUserAndGame(userId, gameId);
            if (userGame == null) {
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");
            }

            userGame.setLastTimePlayed(dateTime);
            return userGameDAO.update(userGame);
    }
    // #endregion Attribute Update Methods

    // #region Read-Only Operations
    public UserGame findByUserAndGame(Long userId, Long gameId) throws ServiceException {
        return userGameDAO.findByUserAndGame(userId, gameId);
    }

    public MyLinkedList<UserGame> findAllByUser(Long userId) throws ServiceException {
        return userGameDAO.findAllByUser(userId);
    }

    public MyLinkedList<UserGame> findByEstimated(Long userId) throws ServiceException {
        return userGameDAO.findByEstimated(userId);
    }

    public MyLinkedList<UserGame> findByGameState(Long userId, UserGameState state) throws ServiceException {
        return userGameDAO.findByGameState(userId, state);
    }
    // #endregion Read-Only Operations
}
