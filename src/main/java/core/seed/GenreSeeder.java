package core.seed;

import service.game.GenreService;

public class GenreSeeder {

    private final GenreService genreService;

    public GenreSeeder(GenreService genreService) {
        this.genreService = genreService;
    }

    public void seed() {
        String[] genres = {
            "Action",
            "Adventure",
            "RPG",
            "Strategy",
            "Simulation",
            "Shooter",
            "Platformer",
            "Puzzle",
            "Racing",
            "Sports",
            "Fighting",
            "Stealth",
            "Survival",
            "Horror",
            "MMORPG",
            "Roguelike",
            "Sandbox",
            "Open World",
            "Soulslike",
            "Rhythm"
        };

        for (String g : genres) {
            genreService.createGenre(g);
        }
    }
}
