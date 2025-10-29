
import model.*;
import service.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final GameService gameService = new GameService();
    private static final GenreService genreService = new GenreService();
    private static final PlatformService platformService = new PlatformService();
    private static final DeveloperService developerService = new DeveloperService();
    private static final UserService userService = new UserService();

    private static User loggedInUser = null; // Guarda o usuário que "fez login"

    public static void main(String[] args) {
        runMainMenu();
        scanner.close();
        System.out.println("Aplicação encerrada.");
    }

    // --- MENUS PRINCIPAIS ---
    private static void runMainMenu() {

        String choice;
        do {

            System.out.println("\n===== GAME DESKTOP MANAGER =====");
            System.out.println("1 - Acessar Biblioteca (Login/Registro)");
            System.out.println("2 - Gerenciar Catálogo (Admin)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        loginOrRegister();
                        if (loggedInUser != null) {
                            runUserMenu();
                        }
                        break;
                    case "2":
                        manageCatalogMenu();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (ValidationException | ServiceException e) {
                System.err.println("\n!!! ERRO: " + e.getMessage() + "\n");
            } catch (Exception e) {
                System.err.println("\n!!! ERRO INESPERADO: " + e.getMessage() + "\n");
                e.printStackTrace();
            }
        } while (!choice.equals("0"));
    }

    private static void runUserMenu() throws ValidationException, ServiceException {
        String choice;
        do {
            printUserMenu();
            choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    viewMyLibrary();
                    break;
                case "2":
                    addGameToMyLibrary();
                    break;
                case "3":
                    removeGameFromMyLibrary();
                    break;
                case "4":
                    loggedInUser = null; // Logout
                    System.out.println("Logout realizado com sucesso.");
                    return; // Retorna ao menu principal
            }
        } while (true);
    }

    // --- LÓGICA DE USUÁRIO E BIBLIOTECA ---
    private static void loginOrRegister() throws ValidationException, ServiceException {
        System.out.println("\n--- Login / Registro ---");
        System.out.print("Digite seu nome de usuário: ");
        String name = scanner.nextLine();

        loggedInUser = userService.findByName(name);

        if (loggedInUser == null) {
            System.out.println("Usuário não encontrado. Deseja registrar um novo usuário com este nome? (s/n)");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("s")) {
                System.out.print("Digite uma senha: ");
                String password = scanner.nextLine();
                loggedInUser = userService.registerUser(name, password);
                System.out.println("Usuário '" + loggedInUser.getname() + "' registrado e logado com sucesso!");
            }
        } else {
            System.out.println("Login bem-sucedido! Bem-vindo(a), " + loggedInUser.getname() + "!");
        }
    }

    private static void viewMyLibrary() throws ServiceException {
        // Para exibir a biblioteca, precisamos recarregar o usuário para inicializar a coleção
        User userWithLibrary = userService.findById(loggedInUser.getId());
        Set<UserGame> library = userWithLibrary.getUserGames();

        System.out.println("\n--- Minha Biblioteca de Jogos (" + library.size() + ") ---");
        if (library.isEmpty()) {
            System.out.println("Sua biblioteca está vazia.");
        } else {
            library.forEach(userGame -> {
                Game game = userGame.getGame();
                System.out.println("ID: " + game.getId() + " | Nome: " + game.getName());
            });
        }
    }

    private static void addGameToMyLibrary() throws ValidationException, ServiceException {
        System.out.println("\n--- Adicionar Jogo à Biblioteca ---");
        listAllGames(); // Mostra todos os jogos disponíveis no catálogo
        System.out.print("Digite o ID do jogo que deseja adicionar: ");
        Long gameId = Long.parseLong(scanner.nextLine());

        userService.addGameToLibrary(loggedInUser.getId(), gameId);
        System.out.println("SUCESSO: Jogo adicionado à sua biblioteca!");
    }

    private static void removeGameFromMyLibrary() throws ValidationException, ServiceException {
        System.out.println("\n--- Remover Jogo da Biblioteca ---");
        viewMyLibrary(); // Mostra os jogos que o usuário já tem
        System.out.print("Digite o ID do jogo que deseja remover: ");
        Long gameId = Long.parseLong(scanner.nextLine());

        userService.removeGameFromLibrary(loggedInUser.getId(), gameId);
        System.out.println("SUCESSO: Jogo removido da sua biblioteca!");
    }

    // --- MENUS E LÓGICA DE ADMINISTRAÇÃO DO CATÁLOGO ---
    // (Esta seção contém os métodos que você já tinha, mas reorganizados)
    private static void manageCatalogMenu() throws ValidationException, ServiceException {
        String choice;
        do {

            System.out.println("\n--- Gerenciar Catálogo (Admin) ---");
            System.out.println("1 - Gerenciar Jogos");
            System.out.println("2 - Gerenciar Gêneros");
            System.out.println("3 - Gerenciar Plataformas");
            System.out.println("4 - Gerenciar Desenvolvedores");
            System.out.println("0 - Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            choice = scanner.nextLine();
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
            }
        } while (!choice.equals("0"));
    }

    // --- MENUS E LÓGICA DE ADMINISTRAÇÃO ---
    private static void manageGamesMenu() throws ServiceException, ValidationException {
        String choice;
        do {
            System.out.println("\n--- Gerenciar Jogos do Catálogo ---");
            System.out.println("1 - Adicionar Novo Jogo");
            System.out.println("2 - Listar Todos os Jogos");
            System.out.println("3 - Buscar Jogo por Nome");
            System.out.println("4 - Editar Jogo");
            System.out.println("5 - Deletar Jogo");
            System.out.print("Escolha uma opção (ou 0 para voltar): ");
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

    private static void manageGenresMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- Gerenciar Gêneros ---");
        System.out.print("Digite o nome do Gênero para criar ou encontrar: ");
        String name = scanner.nextLine();
        Genre genre = genreService.createOrFind(name);
        System.out.println("Gênero '" + genre.getName() + "' processado com ID: " + genre.getId());
    }

    private static void managePlatformsMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- Gerenciar Plataformas ---");
        System.out.print("Digite o nome da Plataforma para criar ou encontrar: ");
        String name = scanner.nextLine();
        Platform platform = platformService.createOrFind(name, null); // Symbol path pode ser adicionado depois
        System.out.println("Plataforma '" + platform.getName() + "' processada com ID: " + platform.getId());
    }

    private static void manageDevelopersMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- Gerenciar Desenvolvedores ---");
        System.out.print("Digite o nome do Desenvolvedor para criar ou encontrar: ");
        String name = scanner.nextLine();
        Developer dev = developerService.createOrFind(name);
        System.out.println("Desenvolvedor '" + dev.getName() + "' processado com ID: " + dev.getId());
    }

    // --- IMPRESSÃO DE MENUS ---
    private static void printUserMenu() {
        System.out.println("\n--- Menu do Usuário: " + loggedInUser.getName() + " ---");
        System.out.println("1 - Ver minha biblioteca");
        System.out.println("2 - Adicionar jogo à minha biblioteca");
        System.out.println("3 - Remover jogo da minha biblioteca");
        System.out.println("4 - Logout (Voltar ao menu principal)");
        System.out.print("Escolha uma opção: ");
    }

    // --- MÉTODOS DE AÇÃO PARA JOGOS (ADMIN) ---
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
