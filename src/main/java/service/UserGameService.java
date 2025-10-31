package service;

import dao.UserGameDAO;
import model.UserGame;
import model.UserGameState;

import java.time.LocalDateTime;
import java.util.List;

public class UserGameService extends BaseService {

    // #region Data Update Methods
    public UserGame updateEstimated(Long userId, Long gameId, boolean estimated) throws ServiceException {
        return executeInTransaction(em -> {
            UserGameDAO dao = new UserGameDAO(em);
            UserGame userGame = dao.findByUserAndGame(userId, gameId);
            if (userGame == null)
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");

            userGame.setEstimated(estimated);
            return dao.update(userGame);
        });
    }

    public UserGame updateGameState(Long userId, Long gameId, UserGameState newState) throws ServiceException {
        return executeInTransaction(em -> {
            UserGameDAO dao = new UserGameDAO(em);
            UserGame userGame = dao.findByUserAndGame(userId, gameId);
            if (userGame == null)
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");

            userGame.setGameState(newState);
            return dao.update(userGame);
        });
    }

    public UserGame updateTotalTimePlayed(Long userId, Long gameId, double newTime) throws ServiceException {
        return executeInTransaction(em -> {
            UserGameDAO dao = new UserGameDAO(em);
            UserGame userGame = dao.findByUserAndGame(userId, gameId);
            if (userGame == null)
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");

            if (newTime < 0)
                throw new ValidationException("O tempo total jogado não pode ser negativo.");

            userGame.setTotaltimePlayed(newTime);
            return dao.update(userGame);
        });
    }

    public UserGame updateLastTimePlayed(Long userId, Long gameId, LocalDateTime dateTime) throws ServiceException {
        return executeInTransaction(em -> {
            UserGameDAO dao = new UserGameDAO(em);
            UserGame userGame = dao.findByUserAndGame(userId, gameId);
            if (userGame == null)
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");

            userGame.setLastTimePlayed(dateTime);
            return dao.update(userGame);
        });
    }

    public UserGame updateAllAttributes(Long userId, Long gameId,
            boolean estimated,
            UserGameState state,
            double totalTimePlayed,
            LocalDateTime lastPlayed)
            throws ServiceException {

        return executeInTransaction(em -> {
            UserGameDAO dao = new UserGameDAO(em);
            UserGame userGame = dao.findByUserAndGame(userId, gameId);
            if (userGame == null)
                throw new ValidationException("Usuário não possui este jogo em sua biblioteca.");

            if (totalTimePlayed < 0)
                throw new ValidationException("O tempo total jogado não pode ser negativo.");

            userGame.setEstimated(estimated);
            userGame.setGameState(state);
            userGame.setTotaltimePlayed(totalTimePlayed);
            userGame.setLastTimePlayed(lastPlayed);

            return dao.update(userGame);
        });
    }
    // #endregion Attribute Update Methods

    // #region Read-Only Operations
    public UserGame findByUserAndGame(Long userId, Long gameId) throws ServiceException {
        return executeReadOnly(em -> new UserGameDAO(em).findByUserAndGame(userId, gameId));
    }

    public List<UserGame> findAllByUser(Long userId) throws ServiceException {
        return executeReadOnly(em -> new UserGameDAO(em).findAllByUser(userId));
    }

    public List<UserGame> findByEstimated(Long userId) throws ServiceException {
        return executeReadOnly(em -> new UserGameDAO(em).findByEstimated(userId));
    }

    public List<UserGame> findByGameState(Long userId, UserGameState state) throws ServiceException {
        return executeReadOnly(em -> new UserGameDAO(em).findByGameState(userId, state));
    }
    // #endregion Read-Only Operations
}