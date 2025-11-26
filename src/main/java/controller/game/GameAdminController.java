package controller.game;

import service.GameService;

import service.GenreService;
import service.PlatformService;
import service.exception.ServiceException;
import service.exception.ValidationException;
import service.DeveloperService;
import util.ConsoleUtils;
import util.Navigation;
import view.GameView;
import view.MenuRenderer;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import model.game.Developer;
import model.game.Game;
import model.game.Genre;
import model.game.Platform;

public class GameAdminController {

    private final GameService gameService;
    private final GenreService genreService;
    private final PlatformService platformService;
    private final DeveloperService developerService;

    public GameAdminController(GameService gameService, GenreService genreService,
            PlatformService platformService, DeveloperService developerService) {
        this.gameService = gameService;
        this.genreService = genreService;
        this.platformService = platformService;
        this.developerService = developerService;
    }

    public void manageGamesMenu() {
        Navigation.push("Game Management Menu");

        while (true) {
            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner(Navigation.getPath());
            MenuRenderer.renderOptions(
                    "1 - Adicionar novo jogo",
                    "2 - Listar todos os jogos",
                    "3 - Buscar jogo por nome",
                    "4 - Buscar jogos por gênero",
                    "5 - Buscar jogos por plataforma",
                    "6 - Buscar jogos por desenvolvedor",
                    "7 - Editar jogo",
                    "8 - Deletar jogo",
                    "0 - Voltar"
            );
            String option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {
                    case "1":
                        addNewGame();
                        ConsoleUtils.waitEnter();
                        break;
                    case "2":
                        GameView.listAll();
                        ConsoleUtils.waitEnter();
                        break;
                    case "3":
                        GameView.buscarJogoPorNome();
                        ConsoleUtils.waitEnter();
                        break;
                    case "4":
                        GameView.listarPorGenero();
                        ConsoleUtils.waitEnter();
                        break;
                    case "5":
                        GameView.listarPorPlataforma();
                        ConsoleUtils.waitEnter();
                        break;
                    case "6":
                        GameView.listarPorDesenvolvedor();
                        ConsoleUtils.waitEnter();
                        break;
                    case "7":
                        editarJogo();
                        ConsoleUtils.waitEnter();
                        break;
                    case "8":
                        deletarJogo();
                        ConsoleUtils.waitEnter();
                        break;
                    case "0":
                        Navigation.pop();
                        return;
                    default:
                        System.out.println("Opção inválida.");
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

    private void addNewGame() throws ServiceException, ValidationException {
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

        System.out.println("\n[ ADICIONAR NOVO JOGO ]");
        String name = ConsoleUtils.readString("Nome do jogo: ");
        LocalDate releaseDate = ConsoleUtils.readData("Data de lançamento (dd/MM/yyyy, ou Enter para vazio): ", null);

        List<Long> genreIds = ConsoleUtils.selecionarMultiplasEntidades(genres, "Gêneros");
        List<Long> platformIds = ConsoleUtils.selecionarMultiplasEntidades(platforms, "Plataformas");
        List<Long> developerIds = ConsoleUtils.selecionarMultiplasEntidades(devs, "Desenvolvedores");

        Game game = gameService.createGame(name, releaseDate, genreIds, platformIds, developerIds);
        System.out.println("Jogo '" + game.getName() + "' criado com ID: " + game.getId());
    }

    private void editarJogo() throws ServiceException, ValidationException {
        Long id = ConsoleUtils.readLong("ID do jogo: ");
        Game game = gameService.findById(id);
        if (game == null) {
            System.out.println("Jogo não encontrado.");
            return;
        }

        GameView.exibirJogoCompleto(game);
        String name = ConsoleUtils.readString("\nNovo nome (Enter para manter '" + game.getName() + "'): ");
        if (name.isEmpty()) {
            name = game.getName();
        }

        LocalDate releaseDate = ConsoleUtils.readData("Nova data (dd/MM/yyyy, ou Enter para manter): ", game.getReleaseDate());

        List<Long> genreIds = ConsoleUtils.selecionarMultiplasEntidades(genreService.findAll(), "Gêneros");
        if (genreIds.isEmpty()) {
            genreIds = game.getGameGenres().stream().map(gg -> gg.getGenre().getId()).collect(Collectors.toList());
        }

        List<Long> platformIds = ConsoleUtils.selecionarMultiplasEntidades(platformService.findAll(), "Plataformas");
        if (platformIds.isEmpty()) {
            platformIds = game.getGamePlatforms().stream().map(gp -> gp.getPlatform().getId())
                    .collect(Collectors.toList());
        }

        List<Long> developerIds = ConsoleUtils.selecionarMultiplasEntidades(developerService.findAll(), "Desenvolvedores");
        if (developerIds.isEmpty()) {
            developerIds = game.getGameDevelopers().stream().map(gd -> gd.getDeveloper().getId())
                    .collect(Collectors.toList());
        }

        Game updated = gameService.updateGame(id, name, releaseDate, genreIds, platformIds, developerIds);
        System.out.println("Jogo '" + updated.getName() + "' atualizado com sucesso!");
    }

    private void deletarJogo() throws ServiceException {
        GameView.listAll();
        Long id = ConsoleUtils.readLong("ID do jogo: ");
        System.out.print("Confirma exclusão? (s/n): ");
        if (ConsoleUtils.readString("").equalsIgnoreCase("s")) {
            gameService.deleteGame(id);
            System.out.println("Jogo deletado com sucesso.");
        } else {
            System.out.println("Operação cancelada.");
        }
    }
}
