package controller;

import model.Game;
import service.GameService;

import service.GenreService;
import service.PlatformService;
import service.DeveloperService;

import service.ServiceException;
import service.ValidationException;

import util.ConsoleUtils;
import view.GameView;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GameAdminController {

    private final GameService gameService = new GameService();
    private final GenreService genreService = new GenreService();
    private final PlatformService platformService = new PlatformService();
    private final DeveloperService developerService = new DeveloperService();

    protected void manageGamesMenu() throws ServiceException, ValidationException {
        String option;
        do {
            System.out.println("\n--- GERENCIAR JOGOS ---");
            System.out.println("1 - Adicionar novo jogo");
            System.out.println("2 - Listar todos os jogos");
            System.out.println("3 - Buscar jogo por nome");
            System.out.println("4 - Buscar jogos por gênero");
            System.out.println("5 - Buscar jogos por plataforma");
            System.out.println("6 - Buscar jogos por desenvolvedor");
            System.out.println("7 - Editar jogo");
            System.out.println("8 - Deletar jogo");
            System.out.println("0 - Voltar");
            option = ConsoleUtils.readString("Escolha uma opção: ");

            switch (option) {
                case "1":
                    addNewGame();
                    break;
                case "2":
                    GameView.listarTodosJogos();
                    break;
                case "3":
                    GameView.buscarJogoPorNome();
                    break;
                case "4":
                    GameView.listarPorGenero();
                    break;
                case "5":
                    GameView.listarPorPlataforma();
                    break;
                case "6":
                    GameView.listarPorDesenvolvedor();
                    break;
                case "7":
                    editarJogo();
                    break;
                case "8":
                    deletarJogo();
                    break;
                case "0":
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (!option.equals("0"));
    }

    private void addNewGame() throws ServiceException, ValidationException {
        System.out.println("\n--- ADICIONAR NOVO JOGO ---");
        String name = ConsoleUtils.readString("Nome do jogo: ");
        LocalDate releaseDate = ConsoleUtils.readData("Data de lançamento (dd/MM/yyyy, ou Enter para vazio): ", null);
        List<Long> genreIds = ConsoleUtils.selecionarMultiplasEntidades(genreService.findAll(), "Gêneros");
        List<Long> platformIds = ConsoleUtils.selecionarMultiplasEntidades(platformService.findAll(), "Plataformas");
        List<Long> developerIds = ConsoleUtils.selecionarMultiplasEntidades(developerService.findAll(), "Desenvolvedores");

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
        String name = ConsoleUtils.readString("Novo nome (Enter para manter '" + game.getName() + "'): ");
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
        GameView.listarTodosJogos();
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
