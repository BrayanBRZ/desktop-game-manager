package core.seed;

import service.game.GameService;
import service.exception.ServiceException;
import java.time.LocalDate;
import utils.MyLinkedList;

public class GameSeeder {

    private final GameService gameService;

    public GameSeeder(GameService gameService) {
        this.gameService = gameService;
    }

    private static class GameSeed {

        String name;
        LocalDate releaseDate;
        MyLinkedList<Long> genres;
        MyLinkedList<Long> platforms;
        MyLinkedList<Long> developers;

        GameSeed(String name, LocalDate releaseDate,
                MyLinkedList<Long> genres, MyLinkedList<Long> platforms, MyLinkedList<Long> developers) {
            this.name = name;
            this.releaseDate = releaseDate;
            this.genres = genres;
            this.platforms = platforms;
            this.developers = developers;
        }
    }

    public void seed() {

        MyLinkedList<GameSeed> seeds = new MyLinkedList<>();

        seeds.add(new GameSeed("The Legend of Zelda: Tears of the Kingdom",
                LocalDate.of(2023, 5, 12),
                MyLinkedList.of(1L),
                MyLinkedList.of(2L, 12L),
                MyLinkedList.of(6L)));

        seeds.add(new GameSeed("Cyberpunk 2077",
                LocalDate.of(2020, 12, 10),
                MyLinkedList.of(3L),
                MyLinkedList.of(1L, 12L, 3L),
                MyLinkedList.of(1L, 2L, 3L, 4L)));

        seeds.add(new GameSeed("Red Dead Redemption",
                LocalDate.of(2010, 5, 18),
                MyLinkedList.of(4L),
                MyLinkedList.of(12L, 1L),
                MyLinkedList.of(1L, 13L, 14L)));

        seeds.add(new GameSeed("Bloodborne",
                LocalDate.of(2015, 3, 24),
                MyLinkedList.of(5L),
                MyLinkedList.of(1L, 11L),
                MyLinkedList.of(2L)));

        seeds.add(new GameSeed("The Elder Scrolls IV: Oblivion",
                LocalDate.of(2006, 3, 20),
                MyLinkedList.of(7L),
                MyLinkedList.of(3L, 12L),
                MyLinkedList.of(1L, 13L, 14L)));

        seeds.add(new GameSeed("Starfield",
                LocalDate.of(2023, 9, 6),
                MyLinkedList.of(7L),
                MyLinkedList.of(3L, 12L, 1L),
                MyLinkedList.of(1L, 4L)));

        seeds.add(new GameSeed("Final Fantasy XVI",
                LocalDate.of(2023, 6, 22),
                MyLinkedList.of(8L),
                MyLinkedList.of(3L, 1L),
                MyLinkedList.of(2L)));

        seeds.add(new GameSeed("Diablo IV",
                LocalDate.of(2023, 6, 5),
                MyLinkedList.of(9L),
                MyLinkedList.of(1L, 3L, 13L),
                MyLinkedList.of(1L, 2L, 3L)));

        seeds.add(new GameSeed("Monster Hunter World",
                LocalDate.of(2018, 1, 26),
                MyLinkedList.of(10L),
                MyLinkedList.of(1L, 11L),
                MyLinkedList.of(1L, 2L, 3L)));

        seeds.add(new GameSeed("Mass Effect 2",
                LocalDate.of(2010, 1, 26),
                MyLinkedList.of(14L),
                MyLinkedList.of(3L, 12L),
                MyLinkedList.of(1L, 13L, 14L)));

        seeds.add(new GameSeed("Halo Infinite",
                LocalDate.of(2021, 12, 8),
                MyLinkedList.of(13L),
                MyLinkedList.of(6L, 1L),
                MyLinkedList.of(1L, 4L)));

        seeds.add(new GameSeed("The Last of Us Part II",
                LocalDate.of(2020, 6, 19),
                MyLinkedList.of(15L),
                MyLinkedList.of(2L, 12L),
                MyLinkedList.of(2L, 3L)));

        seeds.add(new GameSeed("Hades",
                LocalDate.of(2020, 9, 17),
                MyLinkedList.of(12L),
                MyLinkedList.of(14L, 1L),
                MyLinkedList.of(1L, 6L, 9L)));

        seeds.add(new GameSeed("Baldur's Gate 3",
                LocalDate.of(2023, 8, 3),
                MyLinkedList.of(20L),
                MyLinkedList.of(3L, 12L),
                MyLinkedList.of(1L, 2L, 4L)));

        seeds.add(new GameSeed("Sekiro: Shadows Die Twice",
                LocalDate.of(2019, 3, 22),
                MyLinkedList.of(5L),
                MyLinkedList.of(1L, 18L),
                MyLinkedList.of(1L, 2L, 3L)));

        seeds.add(new GameSeed("Shadow of the Colossus",
                LocalDate.of(2005, 10, 18),
                MyLinkedList.of(15L),
                MyLinkedList.of(2L, 12L),
                MyLinkedList.of(18L)));

        seeds.add(new GameSeed("Street Fighter VI",
                LocalDate.of(2023, 6, 2),
                MyLinkedList.of(10L),
                MyLinkedList.of(15L),
                MyLinkedList.of(1L, 2L, 3L)));

        seeds.add(new GameSeed("Persona 5 Royal",
                LocalDate.of(2019, 10, 31),
                MyLinkedList.of(8L, 12L),
                MyLinkedList.of(3L, 2L),
                MyLinkedList.of(1L, 2L, 6L)));

        seeds.add(new GameSeed("Elden Ring",
                LocalDate.of(2022, 2, 25),
                MyLinkedList.of(5L, 20L),
                MyLinkedList.of(1L, 12L, 3L),
                MyLinkedList.of(1L, 2L, 3L)));

        seeds.add(new GameSeed("Metroid Dread",
                LocalDate.of(2021, 10, 8),
                MyLinkedList.of(1L),
                MyLinkedList.of(20L, 7L),
                MyLinkedList.of(6L)));

        seeds.add(new GameSeed("Gears 5",
                LocalDate.of(2019, 9, 10),
                MyLinkedList.of(13L),
                MyLinkedList.of(6L, 1L),
                MyLinkedList.of(1L, 4L)));

        seeds.add(new GameSeed("Crash Bandicoot 4",
                LocalDate.of(2020, 10, 2),
                MyLinkedList.of(15L),
                MyLinkedList.of(7L, 1L),
                MyLinkedList.of(1L, 2L, 3L, 6L)));

        seeds.add(new GameSeed("Hollow Knight: Silksong",
                LocalDate.of(2025, 2, 1),
                MyLinkedList.of(12L),
                MyLinkedList.of(20L, 7L),
                MyLinkedList.of(1L, 6L, 9L)));

        seeds.add(new GameSeed("Dragon's Dogma II",
                LocalDate.of(2024, 3, 22),
                MyLinkedList.of(10L),
                MyLinkedList.of(1L, 3L, 12L),
                MyLinkedList.of(1L, 2L, 3L)));

        seeds.add(new GameSeed("Control",
                LocalDate.of(2019, 8, 27),
                MyLinkedList.of(17L),
                MyLinkedList.of(1L, 11L),
                MyLinkedList.of(1L, 2L, 3L)));

        seeds.add(new GameSeed("Ori and the Will of the Wisps",
                LocalDate.of(2020, 3, 11),
                MyLinkedList.of(13L),
                MyLinkedList.of(20L, 7L),
                MyLinkedList.of(1L, 4L, 6L)));

        seeds.add(new GameSeed("Forza Motorsport",
                LocalDate.of(2023, 10, 10),
                MyLinkedList.of(17L),
                MyLinkedList.of(9L),
                MyLinkedList.of(4L, 1L)));

        seeds.add(new GameSeed("Metroid Prime Remastered",
                LocalDate.of(2023, 2, 8),
                MyLinkedList.of(1L),
                MyLinkedList.of(2L, 7L),
                MyLinkedList.of(6L)));

        seeds.add(new GameSeed("Death Stranding",
                LocalDate.of(2019, 11, 8),
                MyLinkedList.of(15L),
                MyLinkedList.of(2L, 12L, 1L),
                MyLinkedList.of(1L, 2L)));

        seeds.add(new GameSeed("Alan Wake 2",
                LocalDate.of(2023, 10, 27),
                MyLinkedList.of(17L),
                MyLinkedList.of(11L, 1L),
                MyLinkedList.of(1L, 2L, 3L)));

        for (GameSeed seed : seeds) {
            try {
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
