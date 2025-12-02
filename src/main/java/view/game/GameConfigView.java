package view.game;

import utils.ConsoleUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import model.game.Developer;
import model.game.Game;
import model.game.Genre;
import model.game.Platform;
import view.BaseView;

public final class GameConfigView extends BaseView {

    public class GameFormDTO {

        public final Long id;
        public final String name;
        public final LocalDate releaseDate;
        public final List<Long> genreIds;
        public final List<Long> platformIds;
        public final List<Long> developerIds;

        public GameFormDTO(
                Long id,
                String name,
                LocalDate releaseDate,
                List<Long> genreIds,
                List<Long> platformIds,
                List<Long> developerIds
        ) {
            this.id = id;
            this.name = name;
            this.releaseDate = releaseDate;
            this.genreIds = genreIds;
            this.platformIds = platformIds;
            this.developerIds = developerIds;
        }
    }

    // Create
    public GameFormDTO promptGameCreation(
            List<Genre> genres,
            List<Platform> platforms,
            List<Developer> devs
    ) {
        System.out.println("\n[ ADICIONAR NOVO JOGO ]");

        String name = ConsoleUtils.readString("Nome do jogo: ");
        LocalDate releaseDate = ConsoleUtils.readData("Data de lançamento (dd/MM/yyyy, ou Enter para vazio): ", null);
        List<Long> genreIds = ConsoleUtils.selecionarMultiplasEntidades(genres, "Gêneros");
        List<Long> platformIds = ConsoleUtils.selecionarMultiplasEntidades(platforms, "Plataformas");
        List<Long> developerIds = ConsoleUtils.selecionarMultiplasEntidades(devs, "Desenvolvedores");

        return new GameFormDTO(
                null,
                name,
                releaseDate,
                genreIds,
                platformIds,
                developerIds);
    }

    // Update
    public GameFormDTO promptGameUpdate(
            Game existingGame,
            List<Genre> genres,
            List<Platform> platforms,
            List<Developer> devs
    ) {
        System.out.println("\n[ ATUALIZAR JOGO ]");

        String name = ConsoleUtils.readString("Novo nome (Enter para manter '" + existingGame.getName() + "'): ");
        if (name.isEmpty()) name = existingGame.getName();

        LocalDate releaseDate = ConsoleUtils.readData(
                "Nova data (dd/MM/yyyy, ou Enter para manter): ",
                existingGame.getReleaseDate()
        );

        List<Long> genreIds = ConsoleUtils.selecionarMultiplasEntidades(genres, "Gêneros");
        if (genreIds.isEmpty()) {
            genreIds = existingGame.getGameGenres().stream().map(gg -> gg.getGenre().getId()).collect(Collectors.toList());
        }

        List<Long> platformIds = ConsoleUtils.selecionarMultiplasEntidades(platforms, "Plataformas");
        if (platformIds.isEmpty()) {
            platformIds = existingGame.getGamePlatforms().stream().map(gp -> gp.getPlatform().getId())
                    .collect(Collectors.toList());
        }

        List<Long> developerIds = ConsoleUtils.selecionarMultiplasEntidades(devs, "Desenvolvedores");
        if (developerIds.isEmpty()) {
            developerIds = existingGame.getGameDevelopers().stream().map(gd -> gd.getDeveloper().getId())
                    .collect(Collectors.toList());
        }

        return new GameFormDTO(
                existingGame.getId(),
                name,
                releaseDate,
                genreIds,
                platformIds,
                developerIds
        );
    }

    public void genericGameFinderString(String msg1, String msg2, Function<String, List<Game>> searchFunction) {
        String input = ConsoleUtils.readString(msg1);
        List<Game> games = searchFunction.apply(input);
        if (games.isEmpty()) {
            renderMessageLine("Nenhum game encontrado para o " + msg2 + "'" + input + "'.");
        }
        displayGameList(games);
    }

    public void genericGameFinderLong(String msg1, String msg2, Function<Long, List<Game>> searchFunction) {
        Long input = ConsoleUtils.readLong(msg1);
        List<Game> games = searchFunction.apply(input);
        if (games.isEmpty()) {
            renderMessageLine("Nenhum game encontrado para o " + msg2 + "'" + input + "'.");
        }
        displayGameList(games);
    }

    public void displayGameList(List<Game> games) {
        if (games.isEmpty()) {
            renderError("Nenhum game encontrado.");
        } else {
            for (Game game : games) {
                displayGame(game);
            }
        }
    }

    public void displayGame(Game game) {
        String generos = game.getGameGenres().stream().map(gg -> gg.getGenre().getName())
                .collect(Collectors.joining(", "));
        String plataformas = game.getGamePlatforms().stream().map(gp -> gp.getPlatform().getName())
                .collect(Collectors.joining(", "));
        String desenvolvedores = game.getGameDevelopers().stream().map(gd -> gd.getDeveloper().getName())
                .collect(Collectors.joining(", "));
        System.out.printf("ID: %d | %s (%s) | Gêneros: [%s] | Plataformas: [%s] | Devs: [%s]%n",
                game.getId(), game.getName(),
                game.getReleaseDate() != null ? ConsoleUtils.formatDate(game.getReleaseDate()) : "N/A",
                generos.isEmpty() ? "Nenhum" : generos,
                plataformas.isEmpty() ? "Nenhum" : plataformas,
                desenvolvedores.isEmpty() ? "Nenhum" : desenvolvedores);
    }
}
