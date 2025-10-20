package main;

import model.*;
import service.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    // Scanner para ler a entrada do usuário em toda a aplicação
    private static final Scanner scanner = new Scanner(System.in);

    // Instâncias dos serviços que serão usados
    private static final GameService gameService = new GameService();
    private static final GenreService genreService = new GenreService();
    private static final PlatformService platformService = new PlatformService();
    private static final DeveloperService developerService = new DeveloperService();

    public static void main(String[] args) {
        runMenu();
    }

    private static void runMenu() {
        String choice;
        do {
            printMainMenu();
            choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        manageGamesMenu();
                        break;
                    case "2":
                        manageGenresMenu();
                        break;
                    case "3":
                        managePlatformsMenu();
                        break;
                    case "4":
                        manageDevelopersMenu();
                        break;
                    case "0":
                        System.out.println("Saindo da aplicação...");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (ServiceException | ValidationException e) {
                // Captura erros dos serviços e os exibe de forma amigável
                System.err.println("\n!!! ERRO: " + e.getMessage() + "\n");
            } catch (Exception e) {
                // Captura qualquer outro erro inesperado
                System.err.println("\n!!! ERRO INESPERADO: " + e.getMessage() + "\n");
                e.printStackTrace();
            }

        } while (!choice.equals("0"));

        // Garante o encerramento limpo da aplicação
        scanner.close();
    }

    // --- MENUS ---
    private static void printMainMenu() {
        System.out.println("\n--- GAME DESKTOP MANAGER ---");
        System.out.println("1 - Gerenciar Jogos");
        System.out.println("2 - Gerenciar Gêneros");
        System.out.println("3 - Gerenciar Plataformas");
        System.out.println("4 - Gerenciar Desenvolvedores");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void manageGamesMenu() throws ServiceException, ValidationException {
        String choice;
        do {
            System.out.println("\n--- Gerenciar Jogos ---");
            System.out.println("1 - Adicionar Novo Jogo");
            System.out.println("2 - Listar Todos os Jogos");
            System.out.println("3 - Buscar Jogo por Nome");
            System.out.println("4 - Editar Jogo");
            System.out.println("5 - Deletar Jogo");
            System.out.println("Escolha uma opção (ou 0 para voltar): ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addNewGame();
                    break;
                case "2":
                    listAllGames();
                    break;
                case "3":
                    searchGameByName();
                    break;
                case "4":
                    editGame();
                    break;
                case "5":
                    deleteGame();
                    break;
            }
        } while (!choice.equals("0"));
    }

    // Menus para Gêneros, Plataformas e Desenvolvedores (simplificados para brevidade)
    private static void manageGenresMenu() throws ServiceException, ValidationException {
        String choice;
        do {
            System.out.println("\n--- Gerenciar Gêneros ---");
            System.out.println("1 - Adicionar/Encontrar Gênero");
            System.out.println("2 - Listar Todos os Gêneros");
            System.out.println("3 - Buscar Gênero por Nome");
            System.out.println("4 - Deletar Gênero");
            System.out.print("Escolha uma opção (ou 0 para voltar): ");
            choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Digite o nome do Gênero: ");
                String name = scanner.nextLine();
                Genre genre = genreService.createOrFind(name);
                System.out.println("Gênero '" + genre.getName() + "' processado com ID: " + genre.getId());

            } else if (choice.equals("2")) {
                List<Genre> genres = genreService.findAll();
                System.out.println("Gêneros encontrados: " + genres.size());
                genres.forEach(g -> System.out.println("ID: " + g.getId() + " - Nome: " + g.getName()));
            }
        } while (!choice.equals("0"));
    }

    private static void managePlatformsMenu() throws ServiceException, ValidationException {
        // Implementação similar ao manageGenresMenu
        System.out.println("\nFuncionalidade de Plataformas a ser implementada.");
    }

    private static void manageDevelopersMenu() throws ServiceException, ValidationException {
        // Implementação similar ao manageGenresMenu
        System.out.println("\nFuncionalidade de Desenvolvedores a ser implementada.");
    }

    // --- MÉTODOS DE AÇÃO PARA JOGOS ---
    private static void addNewGame() throws ServiceException, ValidationException {
        System.out.println("\n--- Adicionar Novo Jogo ---");
        System.out.print("Nome do Jogo: ");
        String name = scanner.nextLine();

        System.out.print("Data de Lançamento (AAAA-MM-DD): ");
        LocalDate releaseDate = null;
        try {
            releaseDate = LocalDate.parse(scanner.nextLine());
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Deixando em branco.");
        }

        List<Long> genreIds = selectMultipleEntities(genreService.findAll(), "Gêneros");
        List<Long> platformIds = selectMultipleEntities(platformService.findAll(), "Plataformas");
        List<Long> developerIds = selectMultipleEntities(developerService.findAll(), "Desenvolvedores");

        Game newGame = gameService.createGame(name, releaseDate, genreIds, platformIds, developerIds);
        System.out.println("\nSUCESSO: Jogo '" + newGame.getName() + "' criado com ID: " + newGame.getId());
    }

    private static void listAllGames() throws ServiceException {
        List<Game> games = gameService.findAllGames();
        System.out.println("\n--- Todos os Jogos (" + games.size() + ") ---");
        if (games.isEmpty()) {
            System.out.println("Nenhum jogo encontrado.");
        } else {
            games.forEach(game -> {
                String genres = game.getGameGenres().stream()
                        .map(gg -> gg.getGenre().getName())
                        .collect(Collectors.joining(", "));
                System.out.println("ID: " + game.getId() + " | Nome: " + game.getName() + " | Gêneros: [" + genres + "]");
            });
        }
    }

    private static void searchGameByName() throws ServiceException {
        System.out.print("\nDigite o termo de busca para o nome do jogo: ");
        String searchTerm = scanner.nextLine();
        List<Game> games = gameService.findGamesByNameContaining(searchTerm);
        System.out.println("\n--- Resultados da Busca (" + games.size() + ") ---");
        if (games.isEmpty()) {
            System.out.println("Nenhum jogo encontrado com o termo '" + searchTerm + "'.");
        } else {
            games.forEach(game -> System.out.println("ID: " + game.getId() + " | Nome: " + game.getName()));
        }
    }

    private static void editGame() throws ServiceException, ValidationException {
        System.out.print("\nDigite o ID do jogo que deseja editar: ");
        Long gameId = Long.parseLong(scanner.nextLine());
        Game gameToUpdate = gameService.findGameById(gameId);

        if (gameToUpdate == null) {
            System.out.println("Erro: Jogo com ID " + gameId + " não encontrado.");
            return;
        }

        System.out.println("Editando o jogo: " + gameToUpdate.getName());
        System.out.print("Novo nome (deixe em branco para manter o atual: '" + gameToUpdate.getName() + "'): ");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            name = gameToUpdate.getName();
        }

        // A lógica para data, gêneros, etc. seria similar, pedindo novos valores.
        // Por simplicidade, vamos reutilizar os dados existentes para as associações.
        List<Long> genreIds = gameToUpdate.getGameGenres().stream().map(gg -> gg.getGenre().getId()).collect(Collectors.toList());
        List<Long> platformIds = gameToUpdate.getGamePlatforms().stream().map(gp -> gp.getPlatform().getId()).collect(Collectors.toList());
        List<Long> developerIds = gameToUpdate.getGameDevelopers().stream().map(gd -> gd.getDeveloper().getId()).collect(Collectors.toList());

        System.out.println("Re-selecione os gêneros (atuais: " + genreIds + "):");
        List<Long> newGenreIds = selectMultipleEntities(genreService.findAll(), "Gêneros");

        Game updatedGame = gameService.updateGame(gameId, name, gameToUpdate.getReleaseDate(), newGenreIds, platformIds, developerIds);
        System.out.println("\nSUCESSO: Jogo '" + updatedGame.getName() + "' atualizado.");
    }

    private static void deleteGame() throws ServiceException {
        System.out.print("\nDigite o ID do jogo que deseja deletar: ");
        Long gameId = Long.parseLong(scanner.nextLine());

        // Confirmação para segurança
        System.out.print("Tem certeza que deseja deletar o jogo com ID " + gameId + "? (s/n): ");
        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("s")) {
            gameService.deleteGameById(gameId);
            System.out.println("SUCESSO: Jogo deletado.");
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    // --- MÉTODOS AUXILIARES ---
    /**
     * Um método genérico para exibir uma lista de entidades e permitir que o
     * usuário selecione várias.
     *
     * @param entities A lista de entidades a serem exibidas (deve ter um método
     * 'getId' e 'getName').
     * @param entityName O nome do tipo de entidade para exibir no prompt (ex:
     * "Gêneros").
     * @return Uma lista de IDs selecionados pelo usuário.
     */
    private static <T> List<Long> selectMultipleEntities(List<T> entities, String entityName) {
        System.out.println("\nSelecione um ou mais " + entityName + " da lista abaixo (separe os IDs por vírgula):");
        if (entities.isEmpty()) {
            System.out.println("Nenhum(a) " + entityName + " cadastrado(a).");
            return new ArrayList<>();
        }

        entities.forEach(entity -> {
            try {
                // Usa reflexão para chamar getId() e getName() em qualquer entidade
                Long id = (Long) entity.getClass().getMethod("getId").invoke(entity);
                String name = (String) entity.getClass().getMethod("getName").invoke(entity);
                System.out.println("ID: " + id + " - Nome: " + name);
            } catch (Exception e) {
                // Ignora entidades que não têm getId/getName
            }
        });

        System.out.print("Digite os IDs (ex: 1,3,4): ");
        String[] idsStr = scanner.nextLine().split(",");
        List<Long> selectedIds = new ArrayList<>();
        for (String idStr : idsStr) {
            if (!idStr.trim().isEmpty()) {
                selectedIds.add(Long.parseLong(idStr.trim()));
            }
        }
        return selectedIds;
    }
}
