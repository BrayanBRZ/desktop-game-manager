package controller.game;

import model.game.*;
import service.game.*;
import service.exception.ServiceException;
import service.exception.ValidationException;
import view.game.GameConfigView;
import dto.GameDTO;
import core.Navigation;
import utils.ConsoleUtils;
import utils.MyLinkedList;

public class GameConfigController {

    private final GameService gameService;
    private final GenreService genreService;
    private final PlatformService platformService;
    private final DeveloperService developerService;

    public GameConfigController(GameService gameService, GenreService genreService,
            PlatformService platformService, DeveloperService developerService) {
        this.gameService = gameService;
        this.genreService = genreService;
        this.platformService = platformService;
        this.developerService = developerService;
    }

    private final GameConfigView gameConfigView = new GameConfigView();

    public void manageGamesMenu() {
        Navigation.push("Game Management Menu");

        while (true) {
            int choice = gameConfigView.renderBanner(
                    "1 - Adicionar jogo",
                    "2 - Editar jogo",
                    "3 - Deletar jogo",
                    "4 - Listar todos os jogos",
                    "5 - Buscar jogo por nome",
                    "6 - Buscar jogos por gênero",
                    "7 - Buscar jogos por plataforma",
                    "8 - Buscar jogos por desenvolvedor",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        createGame();
                        ConsoleUtils.waitEnter();
                        break;
                    case 2:
                        updateGame();
                        ConsoleUtils.waitEnter();
                        break;
                    case 3:
                        deleteGame();
                        ConsoleUtils.waitEnter();
                        break;
                    case 4:
                        gameConfigView.displayGameList(gameService.findAll());
                        ConsoleUtils.waitEnter();
                        break;
                    case 5:
                        gameConfigView.genericGameFinderString(
                                "Termo de busca: ",
                                "termo",
                                gameService::findByNameContaining);
                        ConsoleUtils.waitEnter();
                        break;
                    case 6:
                        gameConfigView.renderEntityList(genreService.findAll());
                        gameConfigView.genericGameFinderLong(
                                "ID do gênero: ",
                                "gênero",
                                gameService::listByGenreId);
                        ConsoleUtils.waitEnter();
                        break;
                    case 7:
                        gameConfigView.renderEntityList(platformService.findAll());
                        gameConfigView.genericGameFinderLong(
                                "ID da plataforma: ",
                                "plataforma",
                                gameService::listByPlatformId);
                        ConsoleUtils.waitEnter();
                        break;
                    case 8:
                        gameConfigView.renderEntityList(developerService.findAll());
                        gameConfigView.genericGameFinderLong(
                                "ID do desenvolvedor: ",
                                "desenvolvedor",
                                gameService::listByDeveloperId);
                        ConsoleUtils.waitEnter();
                        break;
                    case 0:
                        Navigation.pop();
                        return;
                    default:
                        gameConfigView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                gameConfigView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                gameConfigView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                gameConfigView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    private void createGame() throws ServiceException, ValidationException {
        MyLinkedList<Genre> genres = genreService.findAll();
        MyLinkedList<Platform> platforms = platformService.findAll();
        MyLinkedList<Developer> devs = developerService.findAll();

        GameDTO dto = gameConfigView.promptGameCreation(genres, platforms, devs);

        Game created;
        created = gameService.createGame(
                dto.getName(),
                dto.getReleaseDate(),
                dto.getGenreIds(),
                dto.getPlatformIds(),
                dto.getDeveloperIds()
        );

        gameConfigView.renderMessageLine("Jogo '" + created.getName() + "' criado com sucesso! ID: " + created.getId());
    }

    private void updateGame() {
        Long id = gameConfigView.readLong("ID do jogo: ");
        Game existing = gameService.findById(id);

        GameDTO dto = gameConfigView.promptGameUpdate(
                existing,
                genreService.findAll(),
                platformService.findAll(),
                developerService.findAll()
        );

        Game updated = gameService.updateGame(
                dto.getId(),
                dto.getName(),
                dto.getReleaseDate(),
                dto.getGenreIds(),
                dto.getPlatformIds(),
                dto.getDeveloperIds()
        );

        gameConfigView.renderMessageLine("Jogo '" + updated.getName() + "' atualizado com sucesso!");
    }

    private void deleteGame() {
        Long id = gameConfigView.readLong("ID do jogo: ");
        if (gameConfigView.readString("Confirma exclusão? (s/n): ").trim().equalsIgnoreCase("s")) {
            gameService.deleteGame(id);
            gameConfigView.renderMessage("Jogo deletado com sucesso.");
        } else {
            gameConfigView.renderMessage("Operação cancelada.");
        }
    }
}
