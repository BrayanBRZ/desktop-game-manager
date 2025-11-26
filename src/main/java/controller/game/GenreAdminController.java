package controller.game;

import core.Navigation;
import model.game.Genre;
import service.exception.ServiceException;
import service.exception.ValidationException;
import service.game.GenreService;
import util.ConsoleUtils;
import view.GenreView;
import view.MenuRenderer;

public class GenreAdminController {

    private final GenreService genreService;

    public GenreAdminController(GenreService genreService) {
        this.genreService = genreService;
    }

    public void manageGenresMenu() {
        Navigation.push("Genre Management Menu");

        while (true) {
            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner(Navigation.getPath());
            MenuRenderer.renderOptions(
                    "1 - Criar ou Encontrar Gênero",
                    "2 - Listar Gêneros",
                    "0 - Voltar"
            );
            String option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {
                    case "1":
                        createOrFindGenre();
                        ConsoleUtils.waitEnter();
                        break;

                    case "2":
                        GenreView.listAll();
                        ConsoleUtils.waitEnter();
                        break;

                    case "0":
                        Navigation.pop();
                        return;

                    default:
                        MenuRenderer.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                MenuRenderer.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                MenuRenderer.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                MenuRenderer.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    private void createOrFindGenre() {
        String name = ConsoleUtils.readString("\nNome do gênero para criar ou encontrar: ");
        Genre genre = genreService.createOrFind(name);
        System.out.println("Gênero '" + genre.getName() + "' processado com ID: " + genre.getId());
    }
}
