package service;

import dao.GameDAO;
import dao.GenreDAO;
import dao.PlatformDAO;
import dao.DeveloperDAO;
import model.Game;
import model.Genre;
import model.Platform;
import model.Developer;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer for managing Game entities.
 *
 * @author Brayan Barros
 * @version 1.0
 * @since 2025-10-08
 */
public class GameService {

    private final EntityManager entityManager;

    // The service uses multiple DAOs to perform its operations.
    private final GameDAO gameDAO;
    private final GenreDAO genreDAO;
    private final PlatformDAO platformDAO;
    private final DeveloperDAO developerDAO;

    private static class ValidatedGameData {

        private final String name;
        private final Set<Genre> genres;
        private final Set<Platform> platforms;
        private final Set<Developer> developers;

        public ValidatedGameData(String name, Set<Genre> genres, Set<Platform> platforms, Set<Developer> developers) {
            this.name = name;
            this.genres = genres;
            this.platforms = platforms;
            this.developers = developers;
        }

        // Getters
        public String getName() {
            return name;
        }

        public Set<Genre> getGenres() {
            return genres;
        }

        public Set<Platform> getPlatforms() {
            return platforms;
        }

        public Set<Developer> getDevelopers() {
            return developers;
        }
    }

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

    // #region CRUD Operations

    public Game createGame(String name, LocalDate releaseDate, List<Long> genreIds, List<Long> platformIds, List<Long> developerIds)
            throws ValidationException, ServiceException {

        ValidatedGameData validatedData = validateAndFetchGameData(name, genreIds, platformIds, developerIds);

        Game newGame = new Game();
        newGame.setName(validatedData.getName());
        newGame.setReleaseDate(releaseDate);
        newGame.setGenres(validatedData.getGenres());
        newGame.setPlatforms(validatedData.getPlatforms());
        newGame.setDevelopers(validatedData.getDevelopers());

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

    public Game updateGame(Long gameId, String name, LocalDate releaseDate, List<Long> genreIds, List<Long> platformIds, List<Long> developerIds)
            throws ValidationException, ServiceException {

        Game gameToUpdate = gameDAO.findById(gameId);
        if (gameToUpdate == null) {
            throw new ValidationException("Game with ID " + gameId + " not found.");
        }

        ValidatedGameData validatedData = validateAndFetchGameData(name, genreIds, platformIds, developerIds);

        gameToUpdate.setName(validatedData.getName());
        gameToUpdate.setReleaseDate(releaseDate);
        gameToUpdate.setGenres(validatedData.getGenres());
        gameToUpdate.setPlatforms(validatedData.getPlatforms());
        gameToUpdate.setDevelopers(validatedData.getDevelopers());

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Game updatedGame = gameDAO.update(gameToUpdate);
            transaction.commit();
            return updatedGame;
        } catch (PersistenceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new ServiceException("Failed to update game with ID " + gameId, e);
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

    // #endregion

    //#region Validation Logic

    private ValidatedGameData validateAndFetchGameData(String name, List<Long> genreIds, List<Long> platformIds, List<Long> developerIds)
            throws ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Game name cannot be empty.");
        }
        if (genreIds == null || genreIds.isEmpty()) {
            throw new ValidationException("Game must have at least one genre.");
        }
        if (platformIds == null || platformIds.isEmpty()) {
            throw new ValidationException("Game must have at least one platform.");
        }
        if (developerIds == null || developerIds.isEmpty()) {
            throw new ValidationException("Game must have at least one developer.");
        }

        Set<Genre> genres = genreIds.stream().map(genreDAO::findById).collect(Collectors.toSet());
        if (genres.contains(null)) {
            throw new ValidationException("One or more genre IDs were not found.");
        }

        Set<Platform> platforms = platformIds.stream().map(platformDAO::findById).collect(Collectors.toSet());
        if (platforms.contains(null)) {
            throw new ValidationException("One or more platform IDs were not found.");
        }

        Set<Developer> developers = developerIds.stream().map(developerDAO::findById).collect(Collectors.toSet());
        if (developers.contains(null)) {
            throw new ValidationException("One or more developer IDs were not found.");
        }

        return new ValidatedGameData(name.trim(), genres, platforms, developers);
    }

    //#endregion

    // --- Read-only methods ---

    // #region Exclusive Finders
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

    // #endregion

    // #region Finders by NAME

    /**
     * Finds a game by its specific name.
     *
     * @param name The name of the game to search for.
     * @return The found Game entity, or {@code null} if no game with that name
     * exists.
     * @throws ServiceException if a database access error occurs.
     */
    public Game findGameByName(String name) throws ServiceException {
        try {
            return gameDAO.findByExactName(name);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding game with ID " + name, e);
        }
    }

    /**
     * Find games by search term.
     *
     * @param searchTerm The text to search for within the game names.
     * @return A list of games that match the search criteria.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findGamesByNameContaining(String searchTerm) throws ServiceException {
        try {
            return gameDAO.findByNameContaining(searchTerm);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding games by search term: " + searchTerm, e);
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
     * Find games by developer.
     *
     * @param developerName The developer of games.
     * @return A list of games by developer.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findGamesByDeveloper(String developerName) throws ServiceException {
        try {
            return gameDAO.findByDeveloper(developerName);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding games by developer: " + developerName, e);
        }
    }

    // #endregion

    // #region Finders by ID
    
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
            throw new ServiceException("Error finding game with ID " + id, e);
        }
    }

    /**
     * Find games by genre ID.
     *
     * @param id The genre ID.
     * @return The found Game, or null if not found.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findGameByGenreId(Long genreId) throws ServiceException {
        try {
            return gameDAO.findByGenreId(genreId);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding game with genre ID " + genreId, e);
        }
    }

    /**
     * Find games by platform ID.
     *
     * @param id The platform ID.
     * @return The found Game, or null if not found.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findGameByPlatformId(Long platformId) throws ServiceException {
        try {
            return gameDAO.findByPlatformId(platformId);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding game with platform ID " + platformId, e);
        }
    }

    /**
     * Find games by developer ID.
     *
     * @param id The developer ID.
     * @return The found Game, or null if not found.
     * @throws ServiceException if a database access error occurs.
     */
    public List<Game> findGameByDeveloperId(Long developerId) throws ServiceException {
        try {
            return gameDAO.findByDeveloperId(developerId);
        } catch (PersistenceException e) {
            throw new ServiceException("Error finding game with developer ID " + developerId, e);
        }
    }

    // #endregion
}
