package view;

import service.GenreService;
import service.exception.ServiceException;

import java.util.List;

import model.game.Genre;

public class GenreView {
    
    private static final GenreService genreService = new GenreService();

    public static void listarGeneros() throws ServiceException {
        List<Genre> genres = genreService.findAll();
        System.out.println("\n--- GÊNEROS DISPONÍVEIS ---");
        if (genres.isEmpty()) {
            System.out.println("Nenhum gênero cadastrado.");
        } else {
            genres.forEach(g -> System.out.println("ID: " + g.getId() + " - " + g.getName()));
        }
    }
}