package core.seed;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.game.Game;
import service.exception.ServiceException;
import service.game.GameService;

public class GameSeeder {

    private final GameService gameService;

    public GameSeeder() {
        this.gameService = new GameService();
    }

    private static class GameSeed {

        String name;
        LocalDate releaseDate;
        List<Long> genres;
        List<Long> platforms;
        List<Long> developers;

        GameSeed(String name, LocalDate releaseDate,
                List<Long> genres, List<Long> platforms, List<Long> developers) {
            this.name = name;
            this.releaseDate = releaseDate;
            this.genres = genres;
            this.platforms = platforms;
            this.developers = developers;
        }
    }

    public void seed() {

        List<GameSeed> seeds = new ArrayList<>();

        seeds.add(new GameSeed(
                "Elder Scrolls V: Skyrim",
                LocalDate.of(2011, 11, 11),
                Arrays.asList(1L, 2L),
                Arrays.asList(1L, 3L),
                Arrays.asList(1L)
        ));

        seeds.add(new GameSeed(
                "God of War",
                LocalDate.of(2018, 4, 20),
                Arrays.asList(3L, 4L),
                Arrays.asList(2L),
                Arrays.asList(2L)
        ));

        seeds.add(new GameSeed(
                "Minecraft",
                LocalDate.of(2011, 11, 18),
                Arrays.asList(5L),
                Arrays.asList(1L, 4L),
                Arrays.asList(3L)
        ));

        seeds.add(new GameSeed(
                "The Witcher 3: Wild Hunt",
                LocalDate.of(2015, 5, 19),
                Arrays.asList(1L, 3L),
                Arrays.asList(1L, 2L, 3L),
                Arrays.asList(4L)
        ));

        seeds.add(new GameSeed(
                "Red Dead Redemption 2",
                LocalDate.of(2018, 10, 26),
                Arrays.asList(2L, 3L),
                Arrays.asList(1L, 2L, 3L),
                Arrays.asList(5L)
        ));

        seeds.add(new GameSeed(
                "Half-Life 2",
                LocalDate.of(2004, 11, 16),
                Arrays.asList(9L, 3L),
                Arrays.asList(1L, 6L, 7L),
                Arrays.asList(6L)
        ));

        seeds.add(new GameSeed(
                "The Legend of Zelda: Breath of the Wild",
                LocalDate.of(2017, 3, 3),
                Arrays.asList(3L, 2L),
                Arrays.asList(4L),
                Arrays.asList(7L)
        ));

        seeds.add(new GameSeed(
                "Dark Souls",
                LocalDate.of(2011, 9, 22),
                Arrays.asList(1L, 2L),
                Arrays.asList(1L, 2L, 3L),
                Arrays.asList(8L)
        ));

        seeds.add(new GameSeed(
                "Super Mario Odyssey",
                LocalDate.of(2017, 10, 27),
                Arrays.asList(3L),
                Arrays.asList(4L),
                Arrays.asList(7L)
        ));

        seeds.add(new GameSeed(
                "Counter-Strike: Global Offensive",
                LocalDate.of(2012, 8, 21),
                Arrays.asList(9L),
                Arrays.asList(1L, 6L, 7L),
                Arrays.asList(6L)
        ));

        seeds.add(new GameSeed(
                "Grand Theft Auto V",
                LocalDate.of(2013, 9, 17),
                Arrays.asList(2L, 3L, 5L),
                Arrays.asList(1L, 2L, 3L),
                Arrays.asList(5L)
        ));

        seeds.add(new GameSeed(
                "Bloodborne",
                LocalDate.of(2015, 3, 24),
                Arrays.asList(1L, 10L),
                Arrays.asList(2L),
                Arrays.asList(8L)
        ));

        seeds.add(new GameSeed(
                "Hollow Knight",
                LocalDate.of(2017, 2, 24),
                Arrays.asList(3L, 2L),
                Arrays.asList(1L, 4L),
                Arrays.asList(7L)
        ));

        seeds.add(new GameSeed(
                "Portal 2",
                LocalDate.of(2011, 4, 19),
                Arrays.asList(3L, 6L),
                Arrays.asList(1L, 6L, 7L),
                Arrays.asList(6L)
        ));

        seeds.add(new GameSeed(
                "The Last of Us",
                LocalDate.of(2013, 6, 14),
                Arrays.asList(3L, 10L),
                Arrays.asList(2L),
                Arrays.asList(2L)
        ));

        seeds.add(new GameSeed(
                "Doom Eternal",
                LocalDate.of(2020, 3, 20),
                Arrays.asList(9L, 4L),
                Arrays.asList(1L, 2L, 3L, 10L),
                Arrays.asList(10L)
        ));

        seeds.add(new GameSeed(
                "League of Legends",
                LocalDate.of(2009, 10, 27),
                Arrays.asList(6L),
                Arrays.asList(1L),
                Arrays.asList(9L)
        ));

        seeds.add(new GameSeed(
                "Forza Horizon 5",
                LocalDate.of(2021, 11, 9),
                Arrays.asList(7L),
                Arrays.asList(3L, 1L),
                Arrays.asList(9L)
        ));

        seeds.add(new GameSeed(
                "FIFA 23",
                LocalDate.of(2022, 9, 27),
                Arrays.asList(8L),
                Arrays.asList(1L, 2L, 3L, 4L),
                Arrays.asList(9L)
        ));

        seeds.add(new GameSeed(
                "Resident Evil 7",
                LocalDate.of(2017, 1, 24),
                Arrays.asList(10L, 3L),
                Arrays.asList(1L, 2L, 3L),
                Arrays.asList(9L)
        ));

        for (GameSeed seed : seeds) {
            try {
                Game exists = gameService.findByName(seed.name);

                if (exists != null) {
                    System.out.println("Game já existe, pulando: " + seed.name);
                    continue;
                }

                System.out.println("Criando game: " + seed.name);

                gameService.createGame(
                        seed.name,
                        seed.releaseDate,
                        seed.genres,
                        seed.platforms,
                        seed.developers
                );

                System.out.println("Criado com sucesso!");

            } catch (ServiceException e) {
                throw new ServiceException("Erro ao criar game " + seed.name + ": " + e.getMessage());
            }
        }
    }
}
