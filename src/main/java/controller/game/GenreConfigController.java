package controller.game;

import core.Navigation;
import model.game.Genre;
import service.exception.ServiceException;
import service.exception.ValidationException;
import service.game.GenreService;
import utils.ConsoleUtils;
import view.game.genreConfigView;

public class GenreConfigController {

    private final GenreService genreService;

    public GenreConfigController(GenreService genreService) {
        this.genreService = genreService;
    }

    private final genreConfigView genreConfigView = new genreConfigView();

    public void manageGenresMenu() {
        Navigation.push("Genre Management Menu");
        String entityName = "Gêneros";

        while (true) {
            int choice = genreConfigView.renderBanner(
                    "1 - Criar ou Encontrar Gênero",
                    "2 - Listar Gêneros",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        createOrFind();
                        ConsoleUtils.waitEnter();
                        break;
                    case 2:
                        genreConfigView.displayEntityList(genreService.findAll(), entityName);
                        ConsoleUtils.waitEnter();
                        break;
                    case 0:
                        Navigation.pop();
                        return;

                    default:
                        genreConfigView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                genreConfigView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                genreConfigView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                genreConfigView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    private void createOrFind() {
        String name = ConsoleUtils.readString("\nNome do gênero para criar ou encontrar: ");
        Genre genre = genreService.createOrFind(name);
        genreConfigView.renderMessageLine("Gênero '" + genre.getName() + "' processado com ID: " + genre.getId());
    }
}
