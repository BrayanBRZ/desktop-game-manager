package service;

import dao.DeveloperDAO;
import dao.GameDAO;
import dao.GenreDAO;
import dao.PlatformDAO;
import model.Developer;
import model.Game;
import model.Genre;
import model.Platform;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.List;

/**
 * Service layer for managing Game entities.
 * This class encapsulates the business logic related to games, coordinates
 * data access operations through DAOs, and manages transactions.
 */
public class GameService {

    private final EntityManager entityManager;

    // The service uses multiple DAOs to perform its operations.
    private final GameDAO gameDAO;
    private final GenreDAO genreDAO;
    private final PlatformDAO platformDAO;
    private final DeveloperDAO developerDAO;

    /**
     * Constructs a GameService and initializes all necessary DAOs.
     * 
     * @param entityManager The EntityManager instance for database interaction.
     */
    public GameService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.gameDAO = new GameDAO(entityManager);
        this.genreDAO = new GenreDAO(entityManager);
        this.platformDAO = new PlatformDAO(entityManager);
        this.developerDAO = new DeveloperDAO(entityManager);
    }

    /**
     * Creates a new game and persists it to the database.
     * This method handles fetching related entities (Genre, Platform, Developer) by
     * their IDs.
     *
     * @param name        The name of the game.
     * @param releaseDate The release date of the game.
     * @param genreId     The ID of the game's genre.
     * @param platformId  The ID of the game's platform.
     * @param developerId The ID of the game's developer.
     * @return The newly created and persisted Game entity.
     * @throws ValidationException if input data is invalid (e.g., name is empty,
     *                             IDs are not found).
     * @throws ServiceException    if a database error occurs.
     */
    public Game createGame(String name, LocalDate releaseDate, Long genreId, Long platformId, Long developerId)
            throws ValidationException, ServiceException {

        // 1. Validation Logic
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Game name cannot be empty.");
        }

        Genre genre = genreDAO.findById(genreId);
        if (genre == null) {
            throw new ValidationException("Genre with ID " + genreId + " not found.");
        }

        Platform platform = platformDAO.findById(platformId);
        if (platform == null) {
            throw new ValidationException("Platform with ID " + platformId + " not found.");
        }

        Developer developer = developerDAO.findById(developerId);
        if (developer == null) {
            throw new ValidationException("Developer with ID " + developerId + " not found.");
        }

        // 2. Business Logic (Creating the entity)
        Game newGame = new Game();
        newGame.setName(name);
        newGame.setReleaseDate(releaseDate);
        newGame.setGenre(genre);
        newGame.setPlatform(platform);
        newGame.setDeveloper(developer);

        // 3. Transaction Management
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            gameDAO.save(newGame);
            transaction.commit();
            return newGame;
        } catch (PersistenceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ServiceException("Failed to create game due to a persistence error.", e);
        }
    }

    /**
     * Deletes a game from the database by its ID.
     *
     * @param gameId The ID of the game to delete.
     * @throws ServiceException if a database error occurs.
     */
    public void deleteGameById(Long gameId) throws ServiceException {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            gameDAO.delete(gameId);
            transaction.commit();
        } catch (PersistenceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ServiceException("Failed to delete game with ID " + gameId, e);
        }
    }

    // --- Read-only methods ---
    // These methods delegate directly to the DAO and usually don't need explicit
    // transaction management.

    /**
     * Finds a game by its ID.
     * 
     * @param id The game ID.
     * @return The found Game, or null if not found.
     * @throws ServiceException if a database access error occurs.
     */
    public Game findGameById(Long id) throws ServiceException {
        try {
            return gameDAO.findById(id);
        } catch (PersistenceException e) {
            // Traduz a exceção de persistência para uma exceção de serviço.
            throw new ServiceException("Error finding game with ID " + id, e);
        }
    }

    /**
     * Retrieves all games from the database.
     * 
     * @return A list of all games.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findAllGames() throws ServiceException {
        try {
            return gameDAO.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Error retrieving all games.", e);
        }
    }

    /**
     * Find games by genre.
     * 
     * @param genreName The genre of games.
     * @return A list of games by genre.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findGamesByGenre(String genreName) throws ServiceException {
        try {
            return gameDAO.findByGenre(genreName);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding games by genre: " + genreName, e);
        }
    }

    /**
     * Find games by platform.
     * 
     * @param platformName The platform of games.
     * @return A list of games by platform.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findGamesByPlatform(String platformName) throws ServiceException {
        try {
            return gameDAO.findByPlatform(platformName);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding games by platform: " + platformName, e);
        }
    }

    /**
     * Find games by rating.
     * 
     * @param minRating The minimum rating of games.
     * @return A list of games by rating.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findGamesWithHighRating(Double minRating) throws ServiceException {
        try {
            return gameDAO.findByRatingGreaterThan(minRating);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding games with high rating.", e);
        }
    }
}