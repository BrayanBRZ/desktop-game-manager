package service;

import dao.*;
import model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GameService extends BaseService {

    // #region CRUD Operations
    public Game createGame(String name, LocalDate releaseDate, List<Long> genreIds, List<Long> platformIds, List<Long> developerIds)
            throws ServiceException {
        return executeInTransaction(em -> {
            GameDAO gameDAO = new GameDAO(em);
            GenreDAO genreDAO = new GenreDAO(em);
            PlatformDAO platformDAO = new PlatformDAO(em);
            DeveloperDAO developerDAO = new DeveloperDAO(em);

            ValidatedGameData data = validateAndFetchGameData(name, genreIds, platformIds, developerIds, genreDAO, platformDAO, developerDAO);

            Game game = new Game();
            game.setName(data.getName());
            game.setReleaseDate(releaseDate);

            data.getGenres().forEach(g -> game.getGameGenres().add(new GameGenre(game, g)));
            data.getPlatforms().forEach(p -> game.getGamePlatforms().add(new GamePlatform(game, p)));
            data.getDevelopers().forEach(d -> game.getGameDevelopers().add(new GameDeveloper(game, d)));

            gameDAO.save(game);
            return game;
        });
    }

    public Game updateGame(Long id, String name, LocalDate releaseDate, List<Long> genreIds, List<Long> platformIds, List<Long> developerIds)
            throws ServiceException {
        return executeInTransaction(em -> {
            GameDAO gameDAO = new GameDAO(em);
            GenreDAO genreDAO = new GenreDAO(em);
            PlatformDAO platformDAO = new PlatformDAO(em);
            DeveloperDAO developerDAO = new DeveloperDAO(em);

            Game existing = gameDAO.findById(id);
            if (existing == null) {
                throw new ValidationException("Jogo com ID " + id + " não encontrado.");
            }

            ValidatedGameData data = validateAndFetchGameData(name, genreIds, platformIds, developerIds, genreDAO, platformDAO, developerDAO);

            existing.setName(data.getName());
            existing.setReleaseDate(releaseDate);

            synchronizeCollection(existing.getGameGenres(), data.getGenres(), GameGenre::getGenre, GameGenre::new, existing);
            synchronizeCollection(existing.getGamePlatforms(), data.getPlatforms(), GamePlatform::getPlatform, GamePlatform::new, existing);
            synchronizeCollection(existing.getGameDevelopers(), data.getDevelopers(), GameDeveloper::getDeveloper, GameDeveloper::new, existing);

            return gameDAO.update(existing);
        });
    }

    public void deleteGame(Long id) throws ServiceException {
        executeInTransaction(em -> {
            new GameDAO(em).delete(id);
            return null;
        });
    }
    // #endregion CRUD Operations

    // #region Helpers
    private static class ValidatedGameData {

        private final String name;
        private final Set<Genre> genres;
        private final Set<Platform> platforms;
        private final Set<Developer> developers;

        ValidatedGameData(String name, Set<Genre> g, Set<Platform> p, Set<Developer> d) {
            this.name = name;
            this.genres = g;
            this.platforms = p;
            this.developers = d;
        }

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

    private ValidatedGameData validateAndFetchGameData(
            String name, List<Long> genreIds, List<Long> platformIds, List<Long> developerIds,
            GenreDAO genreDAO, PlatformDAO platformDAO, DeveloperDAO developerDAO
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

        Set<Genre> genres = genreIds.stream().map(genreDAO::findById).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Platform> platforms = platformIds.stream().map(platformDAO::findById).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Developer> developers = developerIds.stream().map(developerDAO::findById).filter(Objects::nonNull).collect(Collectors.toSet());

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

    private <J, T> void synchronizeCollection(Collection<J> current, Collection<T> newTargets,
            Function<J, T> extractor, BiFunction<Game, T, J> creator, Game game) {
        current.removeIf(j -> !newTargets.contains(extractor.apply(j)));
        Set<T> currentTargets = current.stream().map(extractor).collect(Collectors.toSet());
        for (T t : newTargets) {
            if (!currentTargets.contains(t)) {
                current.add(creator.apply(game, t));
            }
        }
    }
    // #endregion Helper Classes and Methods

    // #region Read-Only Operations
    // Exclusive Finders
    public List<Game> listAll() throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findAll());
    }

    public Game findById(Long id) throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findById(id));
    }

    public List<Game> searchByName(String name) throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findByNameContaining(name));
    }

    // Finders by RELATED ENTITY NAME
    public List<Game> listByGenreName(String genre) throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findByGenreName(genre));
    }

    public List<Game> listByDeveloperName(String dev) throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findByDeveloperName(dev));
    }

    public List<Game> listByPlatformName(String platform) throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findByPlatformName(platform));
    }

    // Finders by RELATED ENTITY ID
    public List<Game> listByGenreId(Long id) throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findByGenreId(id));
    }

    public List<Game> listByDeveloperId(Long id) throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findByDeveloperId(id));
    }

    public List<Game> listByPlatformId(Long id) throws ServiceException {
        return executeReadOnly(em -> new GameDAO(em).findByPlatformId(id));
    }
    // #endregion Read-Only Operations
}
