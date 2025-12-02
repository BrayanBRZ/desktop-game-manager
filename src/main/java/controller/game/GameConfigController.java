package controller.game;

import service.exception.ServiceException;
import service.exception.ValidationException;
import service.game.DeveloperService;
import service.game.GameService;
import service.game.GenreService;
import service.game.PlatformService;
import utils.ConsoleUtils;

import java.util.List;

import core.Navigation;
import model.game.Developer;
import model.game.Game;
import model.game.Genre;
import model.game.Platform;
import view.game.GameConfigView;
import view.game.GameConfigView.GameFormDTO;

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
                        gameConfigView.renderListEntity(genreService.findAll());
                        gameConfigView.genericGameFinderLong(
                                "ID do gênero: ",
                                "gênero",
                                gameService::listByGenreId);
                        ConsoleUtils.waitEnter();
                        break;
                    case 7:
                        gameConfigView.renderListEntity(platformService.findAll());
                        gameConfigView.genericGameFinderLong(
                                "ID da plataforma: ",
                                "plataforma",
                                gameService::listByPlatformId);
                        ConsoleUtils.waitEnter();
                        break;
                    case 8:
                        gameConfigView.renderListEntity(developerService.findAll());
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
        List<Genre> genres = genreService.findAll();
        List<Platform> platforms = platformService.findAll();
        List<Developer> devs = developerService.findAll();

        if (genres.isEmpty()) {
            throw new ValidationException("Não é possível criar jogo - nenhum gênero cadastrado.");
        }

        if (platforms.isEmpty()) {
            throw new ValidationException("Não é possível criar jogo - nenhuma plataforma cadastrada.");
        }

        if (devs.isEmpty()) {
            throw new ValidationException("Não é possível criar jogo - nenhum desenvolvedor cadastrado.");
        }

        GameConfigView.GameFormDTO dto = gameConfigView.promptGameCreation(genres, platforms, devs);

        Game created = gameService.createGame(
                dto.name,
                dto.releaseDate,
                dto.genreIds,
                dto.platformIds,
                dto.developerIds
        );

        gameConfigView.renderMessageLine("Jogo '" + created.getName() + "' criado com sucesso! ID: " + created.getId());
    }

    private void updateGame() {
        Long id = ConsoleUtils.readLong("ID do jogo: ");
        Game existing = gameService.findById(id);

        if (existing == null) {
            gameConfigView.renderMessageLine("Jogo não encontrado.");
            return;
        }

        GameFormDTO dto = gameConfigView.promptGameUpdate(
                existing,
                genreService.findAll(),
                platformService.findAll(),
                developerService.findAll()
        );

        Game updated = gameService.updateGame(
                dto.id,
                dto.name,
                dto.releaseDate,
                dto.genreIds,
                dto.platformIds,
                dto.developerIds
        );

        gameConfigView.renderMessageLine("Jogo '" + updated.getName() + "' atualizado com sucesso!");
    }

    private void deleteGame() {
        Long id = ConsoleUtils.readLong("ID do jogo: ");
        gameConfigView.renderMessage("Confirma exclusão? (s/n): ");
        if (ConsoleUtils.readString("").equalsIgnoreCase("s")) {
            gameService.deleteGame(id);
            gameConfigView.renderMessage("Jogo deletado com sucesso.");
        } else {
            gameConfigView.renderMessage("Operação cancelada.");
        }
    }
}
