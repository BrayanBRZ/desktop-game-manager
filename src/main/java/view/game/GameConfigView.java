package view.game;

import model.game.Developer;
import model.game.Game;
import model.game.Genre;
import model.game.Platform;
import view.BaseView;
import utils.ConsoleUtils;
import utils.MyLinkedList;
import java.time.LocalDate;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class GameConfigView extends BaseView {

    public class GameFormDTO {

        public final Long id;
        public final String name;
        public final LocalDate releaseDate;
        public final MyLinkedList<Long> genreIds;
        public final MyLinkedList<Long> platformIds;
        public final MyLinkedList<Long> developerIds;

        public GameFormDTO(
                Long id,
                String name,
                LocalDate releaseDate,
                MyLinkedList<Long> genreIds,
                MyLinkedList<Long> platformIds,
                MyLinkedList<Long> developerIds
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
            MyLinkedList<Genre> genres,
            MyLinkedList<Platform> platforms,
            MyLinkedList<Developer> devs
    ) {
        System.out.println("\n[ ADICIONAR NOVO JOGO ]");

        String name = ConsoleUtils.readString("Nome do jogo: ", null);
        LocalDate releaseDate = ConsoleUtils.readData("Data de lançamento (dd/MM/yyyy, ou Enter para vazio): ", null);
        MyLinkedList<Long> genreIds = ConsoleUtils.selecionarMultiplasEntidades(genres, "Gêneros");
        MyLinkedList<Long> platformIds = ConsoleUtils.selecionarMultiplasEntidades(platforms, "Plataformas");
        MyLinkedList<Long> developerIds = ConsoleUtils.selecionarMultiplasEntidades(devs, "Desenvolvedores");

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
            MyLinkedList<Genre> genres,
            MyLinkedList<Platform> platforms,
            MyLinkedList<Developer> devs
    ) {
        renderMessageLine("[ ATUALIZAR JOGO ]");

        String name = ConsoleUtils.readString(
            "Novo nome (Enter para manter '" + existingGame.getName() + "'): ", 
            existingGame.getName()
        );

        LocalDate releaseDate = ConsoleUtils.readData(
                "Nova data (dd/MM/yyyy, ou Enter para manter): ",
                existingGame.getReleaseDate().toString()
        );

        MyLinkedList<Long> genreIds = ConsoleUtils.selecionarMultiplasEntidades(genres, "Gêneros");
        if (genreIds.isEmpty()) {
            genreIds = MyLinkedList.fromJavaList(
                    existingGame.getGameGenres().stream().map(gg -> gg.getGenre().getId()).collect(Collectors.toList())
            );
        }

        MyLinkedList<Long> platformIds = ConsoleUtils.selecionarMultiplasEntidades(platforms, "Plataformas");
        if (platformIds.isEmpty()) {
            platformIds = MyLinkedList.fromJavaList(
                    existingGame.getGamePlatforms().stream().map(gp -> gp.getPlatform().getId()).collect(Collectors.toList())
            );
        }

        MyLinkedList<Long> developerIds = ConsoleUtils.selecionarMultiplasEntidades(devs, "Desenvolvedores");
        if (developerIds.isEmpty()) {
            developerIds = MyLinkedList.fromJavaList(
                    existingGame.getGameDevelopers().stream().map(gd -> gd.getDeveloper().getId()).collect(Collectors.toList())
            );
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

    public void genericGameFinderString(String msg1, String msg2, Function<String, MyLinkedList<Game>> searchFunction) {
        String input = ConsoleUtils.readString(msg1, null);
        MyLinkedList<Game> games = searchFunction.apply(input);
        if (games.isEmpty()) {
            renderMessageLine("Nenhum game encontrado para o " + msg2 + "'" + input + "'.");
        }
        displayGameList(games);
    }

    public void genericGameFinderLong(String msg1, String msg2, Function<Long, MyLinkedList<Game>> searchFunction) {
        Long input = ConsoleUtils.readLong(msg1, null);
        MyLinkedList<Game> games = searchFunction.apply(input);
        if (games.isEmpty()) {
            renderMessageLine("Nenhum game encontrado para o " + msg2 + "'" + input + "'.");
        }
        displayGameList(games);
    }

    public void displayGameList(MyLinkedList<Game> games) {
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
