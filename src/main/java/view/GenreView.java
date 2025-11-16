package view;

import model.Genre;
import service.GenreService;

import service.ServiceException;

import java.util.List;

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