package service;

import dao.*;
import model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Collection;
import java.util.function.*;
import java.util.stream.Collectors;

public class GameService {

    private final GameDAO gameDAO;
    private final GenreDAO genreDAO;
    private final PlatformDAO platformDAO;
    private final DeveloperDAO developerDAO;

    public GameService() {
        this.gameDAO = new GameDAO();
        this.genreDAO = new GenreDAO();
        this.platformDAO = new PlatformDAO();
        this.developerDAO = new DeveloperDAO();
    }

    // #region CRUD Operations
    public Game createGame(String name, LocalDate releaseDate,
                           List<Long> genreIds, List<Long> platformIds, List<Long> developerIds)
            throws ValidationException {

        return gameDAO.executeInTransaction(manager -> {

            ValidatedGameData data = validateAndFetchGameData(
                    name, genreIds, platformIds, developerIds
            );

            Game game = new Game();
            game.setName(data.getName());
            game.setReleaseDate(releaseDate);

            // Adiciona associações
            data.getGenres().forEach(g -> game.getGameGenres().add(new GameGenre(game, g)));
            data.getPlatforms().forEach(p -> game.getGamePlatforms().add(new GamePlatform(game, p)));
            data.getDevelopers().forEach(d -> game.getGameDevelopers().add(new GameDeveloper(game, d)));

            gameDAO.save(game);
            return game;
        });
    }

    public Game updateGame(Long id, String name, LocalDate releaseDate,
                           List<Long> genreIds, List<Long> platformIds, List<Long> developerIds)
            throws ValidationException {

        return gameDAO.executeInTransaction(manager -> {

            if (id == null) {
                throw new ValidationException("ID do jogo é obrigatório.");
            }

            Game existing = gameDAO.findById(id);
            if (existing == null) {
                throw new ValidationException("Jogo com ID " + id + " não encontrado.");
            }

            ValidatedGameData data = validateAndFetchGameData(
                    name, genreIds, platformIds, developerIds
            );

            existing.setName(data.getName());
            existing.setReleaseDate(releaseDate);

            synchronizeCollection(existing.getGameGenres(), data.getGenres(),
                    GameGenre::getGenre, GameGenre::new, existing);
            synchronizeCollection(existing.getGamePlatforms(), data.getPlatforms(),
                    GamePlatform::getPlatform, GamePlatform::new, existing);
            synchronizeCollection(existing.getGameDevelopers(), data.getDevelopers(),
                    GameDeveloper::getDeveloper, GameDeveloper::new, existing);

            return gameDAO.update(existing);
        });
    }

    public void deleteGame(Long id) {
        gameDAO.performInTransaction(manager -> {
            gameDAO.delete(id);
        });
    }
    // #endregion CRUD Operations


    // #region Read-Only Operations
    public Game findById(Long id) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findById(id));
    }

    public List<Game> findAll() {
        return gameDAO.executeReadOnly(manager -> gameDAO.findAll());
    }

    public List<Game> findByNameContaining(String term) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findByNameContaining(term));
    }

    public List<Game> listByGenreName(String genre) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findByGenreName(genre));
    }

    public List<Game> listByPlatformName(String platform) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findByPlatformName(platform));
    }

    public List<Game> listByDeveloperName(String dev) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findByDeveloperName(dev));
    }

    public List<Game> listByGenreId(Long id) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findByGenreId(id));
    }

    public List<Game> listByPlatformId(Long id) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findByPlatformId(id));
    }

    public List<Game> listByDeveloperId(Long id) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findByDeveloperId(id));
    }

    public List<Game> listByRatingGreaterThan(Double minRating) {
        return gameDAO.executeReadOnly(manager -> gameDAO.findByRatingGreaterThan(minRating));
    }
    // #endregion Read-Only Operations


    // #region Helpers
    private static class ValidatedGameData {
        private final String name;
        private final Set<Genre> genres;
        private final Set<Platform> platforms;
        private final Set<Developer> developers;

        ValidatedGameData(String name, Set<Genre> genres,
                          Set<Platform> platforms, Set<Developer> developers) {
            this.name = name;
            this.genres = genres;
            this.platforms = platforms;
            this.developers = developers;
        }

        public String getName() { return name; }
        public Set<Genre> getGenres() { return genres; }
        public Set<Platform> getPlatforms() { return platforms; }
        public Set<Developer> getDevelopers() { return developers; }
    }

    private ValidatedGameData validateAndFetchGameData(
            String name, List<Long> genreIds, List<Long> platformIds, List<Long> developerIds
    ) throws ValidationException {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Nome do jogo não pode estar vazio.");
        }
        if (genreIds == null || genreIds.isEmpty()) {
            throw new ValidationException("O jogo precisa de pelo menos um gênero.");
        }
        if (platformIds == null || platformIds.isEmpty()) {
            throw new ValidationException("O jogo precisa de pelo menos uma plataforma.");
        }
        if (developerIds == null || developerIds.isEmpty()) {
            throw new ValidationException("O jogo precisa de pelo menos um desenvolvedor.");
        }

        Set<Genre> genres = genreIds.stream()
                .map(genreDAO::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Platform> platforms = platformIds.stream()
                .map(platformDAO::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Developer> developers = developerIds.stream()
                .map(developerDAO::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (genres.size() != genreIds.size()) {
            throw new ValidationException("Alguns gêneros informados não existem.");
        }
        if (platforms.size() != platformIds.size()) {
            throw new ValidationException("Algumas plataformas informadas não existem.");
        }
        if (developers.size() != developerIds.size()) {
            throw new ValidationException("Alguns desenvolvedores informados não existem.");
        }

        return new ValidatedGameData(name.trim(), genres, platforms, developers);
    }

    private <J, T> void synchronizeCollection(
            Collection<J> current, Collection<T> newTargets,
            Function<J, T> extractor, BiFunction<Game, T, J> creator, Game game) {

        current.removeIf(j -> !newTargets.contains(extractor.apply(j)));
        Set<T> currentTargets = current.stream().map(extractor).collect(Collectors.toSet());

        for (T t : newTargets) {
            if (!currentTargets.contains(t)) {
                current.add(creator.apply(game, t));
            }
        }
    }
    // #endregion Helpers
}
