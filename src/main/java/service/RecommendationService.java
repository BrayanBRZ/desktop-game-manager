// service/RecommendationService.java
package service;

import dao.GameDAO;
import dao.UserDAO;
import model.Game;
import model.User;
import model.UserGame;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationService {

    private final UserDAO userDAO = new UserDAO();
    private final GameDAO gameDAO = new GameDAO();

    /**
     * Recomendações inteligentes com fallback.
     * Prioridade:
     * 1. Jogos que os amigos do usuário possuem (e ele não)
     * 2. Jogos mais populares da plataforma (fallback)
     *
     * @param userId ID do usuário logado
     * @param limit  Quantidade máxima de recomendações
     * @return Lista de jogos recomendados
     */
    public List<Game> getRecommendations(Long userId, int limit) throws ServiceException {
        if (limit <= 0) limit = 6;

        User user = userDAO.findById(userId);
        if (user == null) {
            throw new ServiceException("Usuário não encontrado.", null);
        }

        Set<Long> userGameIds = user.getUserGames().stream()
                .map(ug -> ug.getGame().getId())
                .collect(Collectors.toSet());

        List<Game> friendBased = getFriendBasedRecommendations(user, userGameIds, limit);
        if (!friendBased.isEmpty()) {
            return friendBased;
        }

        // Fallback: jogos mais populares da plataforma
        return getPopularGamesDynamic(userGameIds, limit);
    }

    /**
     * Recomendações baseadas apenas nos amigos (a original que você queria)
     */
    public List<Game> getFriendBasedRecommendations(Long userId, int limit) throws ServiceException {
        User user = userDAO.findById(userId);
        if (user == null) throw new ServiceException("Usuário não encontrado.", null);
        Set<Long> userGameIds = user.getUserGames().stream()
                .map(ug -> ug.getGame().getId())
                .collect(Collectors.toSet());

        return getFriendBasedRecommendations(user, userGameIds, limit);
    }

    // Método privado reutilizado
    private List<Game> getFriendBasedRecommendations(User user, Set<Long> userGameIds, int limit) {
        if (user.getFriends().isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> gameScore = new HashMap<>(); // gameId → soma de relevância

        for (User friend : user.getFriends()) {
            for (UserGame ug : friend.getUserGames()) {
                Long gameId = ug.getGame().getId();
                if (userGameIds.contains(gameId)) continue; // já tem

                long score = 1L;

                // Bônus: se o amigo completou o jogo
                if (ug.getGameState() != null && ug.getGameState().toString().contains("COMPLETED")) {
                    score += 3;
                }

                // Bônus: quanto mais tempo jogado, mais relevante
                if (ug.getTotaltimePlayed() > 50) score += 2;
                else if (ug.getTotaltimePlayed() > 20) score += 1;

                gameScore.merge(gameId, score, Long::sum);
            }
        }

        return gameScore.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> gameDAO.findById(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Versão alternativa caso Game não tenha ownersCount (calcula na hora)
    private List<Game> getPopularGamesDynamic(Set<Long> excludeGameIds, int limit) {
        Map<Long, Integer> popularity = new HashMap<>();

        for (User u : userDAO.findAll()) {
            for (UserGame ug : u.getUserGames()) {
                Long gameId = ug.getGame().getId();
                if (!excludeGameIds.contains(gameId)) {
                    popularity.merge(gameId, 1, Integer::sum);
                }
            }
        }

        return popularity.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(e -> gameDAO.findById(e.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}