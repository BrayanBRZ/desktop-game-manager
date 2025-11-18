package controller.admin;

import model.game.Genre;
import service.GenreService;
import service.exception.ServiceException;
import service.exception.ValidationException;
import util.ConsoleUtils;
import view.GenreView;

public class GenreAdminController {

    private final GenreService genreService;

    public GenreAdminController(GenreService genreService) {
        this.genreService = genreService;
    }

    public void manageGenresMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- GERENCIAR GÊNEROS ---");
        GenreView.listarGeneros();
        String name = ConsoleUtils.readString("Nome do gênero para criar ou encontrar: ");
        Genre genre = genreService.createOrFind(name);
        System.out.println("Gênero '" + genre.getName() + "' processado com ID: " + genre.getId());
    }
}
