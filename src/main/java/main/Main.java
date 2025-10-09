package main;

import model.Game;
import service.GameService;
import service.ServiceException;

import javax.persistence.EntityManager;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        EntityManager em = null;
        try {

            // 2. Instantiate the service layer
            GameService gameService = new GameService();

            // 3. Use the service to perform an operation (e.g., find all games)
            System.out.println("Fetching all games from the database...");
            List<Game> games = gameService.findAllGames();

            if (games.isEmpty()) {
                System.out.println("No games found in the database.");
            } else {
                System.out.println("Found " + games.size() + " games:");
                for (Game game : games) {
                    System.out.println("- " + game.getName());
                }
            }

        } catch (ServiceException e) {
            System.err.println("A service error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 4. Always close the EntityManager
            if (em != null) {
                em.close();
            }
        }
    }
}