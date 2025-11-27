package core.seed;

import service.game.GenreService;

public class GenreSeeder {

    private final GenreService genreService;

    public GenreSeeder() {
        this.genreService = new GenreService();
    }

    public void seed() {
        String[] genres = {
            "Action", "Adventure", "RPG", "Strategy", "Simulation",
            "Shooter", "Platformer", "Puzzle", "Racing", "Sports"
        };

        for (String g : genres) {
            genreService.createGenre(g);
        }
    }
}
