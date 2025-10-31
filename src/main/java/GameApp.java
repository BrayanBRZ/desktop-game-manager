import model.*;
import service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class GameApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final GameService gameService = new GameService();
    private static final UserService userService = new UserService();
    private static final UserGameService userGameService = new UserGameService();
    private static final GenreService genreService = new GenreService();
    private static final PlatformService platformService = new PlatformService();
    private static final DeveloperService developerService = new DeveloperService();

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static User loggedInUser = null;

    public static void main(String[] args) {
        System.out.println("=== Game Desktop Manager ===");
        runMainMenu();
        scanner.close();
        System.out.println("Aplicação encerrada.");
    }

    // --- MENUS PRINCIPAIS ---
    private static void runMainMenu() {
        String choice;
        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Acessar Biblioteca (Login/Registro)");
            System.out.println("2 - Gerenciar Catálogo (Admin)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        loginOrRegister();
                        break;
                    case "2":
                        manageCatalogMenu();
                        break;
                    case "0":
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (ServiceException | ValidationException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Erro inesperado: " + e.getMessage());
                e.printStackTrace();
            }
        } while (!choice.equals("0"));
    }

    private static void loginOrRegister() throws ServiceException, ValidationException {
        System.out.println("\n--- LOGIN / REGISTRO ---");
        String name = lerString("Nome de usuário: ");
        User user = userService.findByName(name);

        if (user == null) {
            System.out.println("Usuário não encontrado. Deseja registrar? (s/n)");
            if (lerString("").equalsIgnoreCase("s")) {
                String password = lerString("Senha: ");
                loggedInUser = userService.registerUser(name, password);
                System.out.println("Usuário '" + loggedInUser.getName() + "' registrado e logado com sucesso!");
                runUserMenu();
            }
        } else {
            String password = lerString("Senha: ");
            loggedInUser = userService.login(name, password);
            if (loggedInUser == null) {
                System.out.println("Credenciais inválidas.");
            } else {
                System.out.println("Bem-vindo(a), " + loggedInUser.getName() + "!");
                runUserMenu();
            }
        }
    }

    private static void runUserMenu() throws ServiceException, ValidationException {
        String choice;
        do {
            System.out.println("\n--- MENU DO USUÁRIO: " + loggedInUser.getName() + " ---");
            System.out.println("1 - Ver minha biblioteca");
            System.out.println("2 - Adicionar jogo à biblioteca");
            System.out.println("3 - Remover jogo da biblioteca");
            System.out.println("4 - Atualizar estado do jogo");
            System.out.println("5 - Atualizar perfil");
            System.out.println("6 - Alterar senha");
            System.out.println("7 - Logout");
            System.out.print("Escolha uma opção: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewMyLibrary();
                    break;
                case "2":
                    addGameToLibrary();
                    break;
                case "3":
                    removeGameFromLibrary();
                    break;
                case "4":
                    updateGameState();
                    break;
                case "5":
                    updateUserProfile();
                    break;
                case "6":
                    changePassword();
                    break;
                case "7":
                    loggedInUser = null;
                    System.out.println("Logout realizado com sucesso.");
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (true);
    }

    private static void manageCatalogMenu() throws ServiceException, ValidationException {
        String choice;
        do {
            System.out.println("\n--- GERENCIAR CATÁLOGO (ADMIN) ---");
            System.out.println("1 - Gerenciar Jogos");
            System.out.println("2 - Gerenciar Gêneros");
            System.out.println("3 - Gerenciar Plataformas");
            System.out.println("4 - Gerenciar Desenvolvedores");
            System.out.println("0 - Voltar");
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
                case "0":
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        } while (!choice.equals("0"));
    }

    // --- LÓGICA DE USUÁRIO ---
    private static void viewMyLibrary() throws ServiceException {
        User user = userService.findById(loggedInUser.getId());
        Set<UserGame> library = user.getUserGames();
        System.out.println("\n--- Minha Biblioteca (" + library.size() + ") ---");
        if (library.isEmpty()) {
            System.out.println("Biblioteca vazia.");
        } else {
            library.forEach(ug -> {
                Game game = ug.getGame();
                System.out.printf("ID: %d | %s | Estado: %s | Tempo: %.1f h | Estimado: %s | Última vez: %s%n",
                        game.getId(), game.getName(), ug.getGameState(),
                        ug.getTotaltimePlayed(), ug.isEstimated() ? "Sim" : "Não",
                        ug.getLastTimePlayed() != null ? ug.getLastTimePlayed().format(DATE_TIME_FORMATTER) : "N/A");
            });
        }
    }

    private static void addGameToLibrary() throws ServiceException, ValidationException {
        System.out.println("\n--- ADICIONAR JOGO À BIBLIOTECA ---");
        listarTodosJogos();
        Long gameId = lerLong("ID do jogo: ");
        userService.addGameToLibrary(loggedInUser.getId(), gameId);
        System.out.println("Jogo adicionado com sucesso!");
    }

    private static void removeGameFromLibrary() throws ServiceException, ValidationException {
        System.out.println("\n--- REMOVER JOGO DA BIBLIOTECA ---");
        viewMyLibrary();
        Long gameId = lerLong("ID do jogo: ");
        userService.removeGameFromLibrary(loggedInUser.getId(), gameId);
        System.out.println("Jogo removido com sucesso!");
    }

    private static void updateGameState() throws ServiceException, ValidationException {
        System.out.println("\n--- ATUALIZAR ESTADO DO JOGO ---");
        viewMyLibrary();
        Long gameId = lerLong("ID do jogo: ");
        UserGame userGame = userGameService.findByUserAndGame(loggedInUser.getId(), gameId);
        if (userGame == null) {
            System.out.println("Jogo não encontrado na biblioteca.");
            return;
        }

        System.out.println("Estado atual: " + userGame.getGameState());
        System.out.println("Estados disponíveis: " + Arrays.toString(UserGameState.values()));
        String stateStr = lerString("Novo estado (ex: NOT_PLAYED, PLAYING, COMPLETED): ");
        UserGameState state;
        try {
            state = UserGameState.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Estado inválido.");
            return;
        }

        String estimatedStr = lerString("Estimado? (s/n): ");
        boolean estimated = estimatedStr.equalsIgnoreCase("s");

        double totalTime = lerDouble("Tempo total jogado (horas): ");
        LocalDateTime lastPlayed = lerDataHora("Última vez jogado (dd/MM/yyyy HH:mm, ou Enter para agora): ",
                LocalDateTime.now());

        userGameService.updateAllAttributes(loggedInUser.getId(), gameId, estimated, state, totalTime, lastPlayed);
        System.out.println("Estado do jogo atualizado com sucesso!");
    }

    private static void updateUserProfile() throws ServiceException, ValidationException {
        System.out.println("\n--- ATUALIZAR PERFIL ---");
        String name = lerString("Novo nome (Enter para manter '" + loggedInUser.getName() + "'): ");
        if (name.isEmpty())
            name = loggedInUser.getName();

        LocalDate birthDate = lerData("Data de nascimento (dd/MM/yyyy, ou Enter para manter): ",
                loggedInUser.getBirthDate());
        String avatarPath = lerString("Caminho do avatar (Enter para manter): ");

        loggedInUser = userService.updateUserProfile(loggedInUser.getId(), name, birthDate,
                avatarPath.isEmpty() ? null : avatarPath);
        System.out.println("Perfil atualizado com sucesso!");
    }

    private static void changePassword() throws ServiceException, ValidationException {
        System.out.println("\n--- ALTERAR SENHA ---");
        String currentPassword = lerString("Senha atual: ");
        String newPassword = lerString("Nova senha: ");
        userService.changePassword(loggedInUser.getId(), currentPassword, newPassword);
        System.out.println("Senha alterada com sucesso!");
    }

    // --- LÓGICA DE ADMINISTRAÇÃO ---
    private static void manageGamesMenu() throws ServiceException, ValidationException {
        String choice;
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
            System.out.print("Escolha uma opção: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addNewGame();
                    break;
                case "2":
                    listarTodosJogos();
                    break;
                case "3":
                    buscarJogoPorNome();
                    break;
                case "4":
                    listarPorGenero();
                    break;
                case "5":
                    listarPorPlataforma();
                    break;
                case "6":
                    listarPorDesenvolvedor();
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
        } while (!choice.equals("0"));
    }

    private static void manageGenresMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- GERENCIAR GÊNEROS ---");
        listarGeneros();
        String name = lerString("Nome do gênero para criar ou encontrar: ");
        Genre genre = genreService.createOrFind(name);
        System.out.println("Gênero '" + genre.getName() + "' processado com ID: " + genre.getId());
    }

    private static void managePlatformsMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- GERENCIAR PLATAFORMAS ---");
        listarPlataformas();
        String name = lerString("Nome da plataforma para criar ou encontrar: ");
        Platform platform = platformService.createOrFind(name);
        System.out.println("Plataforma '" + platform.getName() + "' processada com ID: " + platform.getId());
    }

    private static void manageDevelopersMenu() throws ServiceException, ValidationException {
        System.out.println("\n--- GERENCIAR DESENVOLVEDORES ---");
        listarDesenvolvedores();
        String name = lerString("Nome do desenvolvedor para criar ou encontrar: ");
        String location = lerString("Localização (Enter para vazio): ");
        String symbolPath = lerString("Caminho do símbolo (Enter para vazio): ");
        Developer dev = developerService.createOrFind(name);
        // Atualiza dados extras se necessário
        if (!location.isEmpty() || !symbolPath.isEmpty()) {
            dev = developerService.updateDeveloper(dev.getId(), name, location.isEmpty() ? null : location,
                    symbolPath.isEmpty() ? null : symbolPath);
        }
        System.out.println("Desenvolvedor '" + dev.getName() + "' processado com ID: " + dev.getId());
    }

    private static void addNewGame() throws ServiceException, ValidationException {
        System.out.println("\n--- ADICIONAR NOVO JOGO ---");
        String name = lerString("Nome do jogo: ");
        LocalDate releaseDate = lerData("Data de lançamento (dd/MM/yyyy, ou Enter para vazio): ", null);
        List<Long> genreIds = selecionarMultiplasEntidades(genreService.findAll(), "Gêneros");
        List<Long> platformIds = selecionarMultiplasEntidades(platformService.findAll(), "Plataformas");
        List<Long> developerIds = selecionarMultiplasEntidades(developerService.findAll(), "Desenvolvedores");

        Game game = gameService.createGame(name, releaseDate, genreIds, platformIds, developerIds);
        System.out.println("Jogo '" + game.getName() + "' criado com ID: " + game.getId());
    }

    private static void listarTodosJogos() throws ServiceException {
        List<Game> jogos = gameService.findAll();
        System.out.println("\n--- TODOS OS JOGOS (" + jogos.size() + ") ---");
        if (jogos.isEmpty()) {
            System.out.println("Nenhum jogo cadastrado.");
        } else {
            jogos.forEach(GameApp::exibirJogoResumido);
        }
    }

    private static void buscarJogoPorNome() throws ServiceException {
        String term = lerString("Termo de busca: ");
        List<Game> jogos = gameService.searchByName(term);
        exibirListaJogos(jogos, "Nenhum jogo encontrado com '" + term + "'.");
    }

    private static void listarPorGenero() throws ServiceException {
        listarGeneros();
        String genre = lerString("Nome do gênero: ");
        List<Game> jogos = gameService.listByGenreName(genre);
        exibirListaJogos(jogos, "Nenhum jogo encontrado para o gênero '" + genre + "'.");
    }

    private static void listarPorPlataforma() throws ServiceException {
        listarPlataformas();
        String platform = lerString("Nome da plataforma: ");
        List<Game> jogos = gameService.listByPlatformName(platform);
        exibirListaJogos(jogos, "Nenhum jogo encontrado para a plataforma '" + platform + "'.");
    }

    private static void listarPorDesenvolvedor() throws ServiceException {
        listarDesenvolvedores();
        String dev = lerString("Nome do desenvolvedor: ");
        List<Game> jogos = gameService.listByDeveloperName(dev);
        exibirListaJogos(jogos, "Nenhum jogo encontrado para o desenvolvedor '" + dev + "'.");
    }

    private static void editarJogo() throws ServiceException, ValidationException {
        listarTodosJogos();
        Long id = lerLong("ID do jogo: ");
        Game game = gameService.findById(id);
        if (game == null) {
            System.out.println("Jogo não encontrado.");
            return;
        }

        exibirJogoCompleto(game);
        String name = lerString("Novo nome (Enter para manter '" + game.getName() + "'): ");
        if (name.isEmpty())
            name = game.getName();

        LocalDate releaseDate = lerData("Nova data (dd/MM/yyyy, ou Enter para manter): ", game.getReleaseDate());

        List<Long> genreIds = selecionarMultiplasEntidades(genreService.findAll(), "Gêneros");
        if (genreIds.isEmpty()) {
            genreIds = game.getGameGenres().stream().map(gg -> gg.getGenre().getId()).collect(Collectors.toList());
        }

        List<Long> platformIds = selecionarMultiplasEntidades(platformService.findAll(), "Plataformas");
        if (platformIds.isEmpty()) {
            platformIds = game.getGamePlatforms().stream().map(gp -> gp.getPlatform().getId())
                    .collect(Collectors.toList());
        }

        List<Long> developerIds = selecionarMultiplasEntidades(developerService.findAll(), "Desenvolvedores");
        if (developerIds.isEmpty()) {
            developerIds = game.getGameDevelopers().stream().map(gd -> gd.getDeveloper().getId())
                    .collect(Collectors.toList());
        }

        Game updated = gameService.updateGame(id, name, releaseDate, genreIds, platformIds, developerIds);
        System.out.println("Jogo '" + updated.getName() + "' atualizado com sucesso!");
    }

    private static void deletarJogo() throws ServiceException {
        listarTodosJogos();
        Long id = lerLong("ID do jogo: ");
        System.out.print("Confirma exclusão? (s/n): ");
        if (lerString("").equalsIgnoreCase("s")) {
            gameService.deleteGame(id);
            System.out.println("Jogo deletado com sucesso.");
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    // --- LISTAGENS ---
    private static void listarGeneros() throws ServiceException {
        List<Genre> genres = genreService.findAll();
        System.out.println("\n--- GÊNEROS DISPONÍVEIS ---");
        if (genres.isEmpty()) {
            System.out.println("Nenhum gênero cadastrado.");
        } else {
            genres.forEach(g -> System.out.println("ID: " + g.getId() + " - " + g.getName()));
        }
    }

    private static void listarPlataformas() throws ServiceException {
        List<Platform> platforms = platformService.findAll();
        System.out.println("\n--- PLATAFORMAS DISPONÍVEIS ---");
        if (platforms.isEmpty()) {
            System.out.println("Nenhuma plataforma cadastrada.");
        } else {
            platforms.forEach(p -> System.out.println("ID: " + p.getId() + " - " + p.getName()));
        }
    }

    private static void listarDesenvolvedores() throws ServiceException {
        List<Developer> devs = developerService.findAll();
        System.out.println("\n--- DESENVOLVEDORES DISPONÍVEIS ---");
        if (devs.isEmpty()) {
            System.out.println("Nenhum desenvolvedor cadastrado.");
        } else {
            devs.forEach(d -> System.out.println("ID: " + d.getId() + " - " + d.getName() +
                    (d.getLocation() != null ? " (" + d.getLocation() + ")" : "")));
        }
    }

    // --- EXIBIÇÃO DE JOGOS ---
    private static void exibirJogoResumido(Game jogo) {
        String generos = jogo.getGameGenres().stream().map(gg -> gg.getGenre().getName())
                .collect(Collectors.joining(", "));
        String plataformas = jogo.getGamePlatforms().stream().map(gp -> gp.getPlatform().getName())
                .collect(Collectors.joining(", "));
        String desenvolvedores = jogo.getGameDevelopers().stream().map(gd -> gd.getDeveloper().getName())
                .collect(Collectors.joining(", "));
        System.out.printf("ID: %d | %s (%s) | Gêneros: [%s] | Plataformas: [%s] | Devs: [%s]%n",
                jogo.getId(), jogo.getName(),
                jogo.getReleaseDate() != null ? jogo.getReleaseDate().format(DATE_FORMATTER) : "N/A",
                generos.isEmpty() ? "Nenhum" : generos,
                plataformas.isEmpty() ? "Nenhum" : plataformas,
                desenvolvedores.isEmpty() ? "Nenhum" : desenvolvedores);
    }

    private static void exibirJogoCompleto(Game jogo) {
        System.out.println("\n=== DETALHES DO JOGO ===");
        System.out.println("ID: " + jogo.getId());
        System.out.println("Nome: " + jogo.getName());
        System.out.println("Lançamento: "
                + (jogo.getReleaseDate() != null ? jogo.getReleaseDate().format(DATE_FORMATTER) : "N/A"));
        System.out.println("Rating: " + (jogo.getRating() != null ? jogo.getRating() : "N/A"));

        System.out.println("Gêneros:");
        jogo.getGameGenres().forEach(
                gg -> System.out.println("  - " + gg.getGenre().getName() + " (ID: " + gg.getGenre().getId() + ")"));

        System.out.println("Plataformas:");
        jogo.getGamePlatforms().forEach(gp -> System.out
                .println("  - " + gp.getPlatform().getName() + " (ID: " + gp.getPlatform().getId() + ")"));

        System.out.println("Desenvolvedores:");
        jogo.getGameDevelopers().forEach(gd -> System.out
                .println("  - " + gd.getDeveloper().getName() + " (ID: " + gd.getDeveloper().getId() + ")"));

        System.out.println("Criado em: " + jogo.getCreatedAt());
        System.out.println("Atualizado em: " + jogo.getUpdatedAt());
    }

    private static void exibirListaJogos(List<Game> jogos, String msgVazio) {
        System.out.println("\n--- RESULTADOS (" + jogos.size() + ") ---");
        if (jogos.isEmpty()) {
            System.out.println(msgVazio);
        } else {
            jogos.forEach(GameApp::exibirJogoResumido);
        }
    }

    // --- MÉTODOS AUXILIARES ---
    private static <T> List<Long> selecionarMultiplasEntidades(List<T> entities, String entityName)
            throws ServiceException {
        System.out.println(
                "\n--- Selecione " + entityName + " (IDs separados por vírgula, ou Enter para manter atuais) ---");
        if (entities.isEmpty()) {
            System.out.println("Nenhum(a) " + entityName + " cadastrado(a).");
            return new ArrayList<>();
        }
        entities.forEach(e -> {
            try {
                Long id = (Long) e.getClass().getMethod("getId").invoke(e);
                String name = (String) e.getClass().getMethod("getName").invoke(e);
                System.out.println("ID: " + id + " - " + name);
            } catch (Exception ex) {
                // Ignora
            }
        });
        return lerListaIds("IDs: ");
    }

    private static String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static Long lerLong(String prompt) {
        while (true) {
            String input = lerString(prompt);
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("ID inválido. Digite um número.");
            }
        }
    }

    private static double lerDouble(String prompt) {
        while (true) {
            String input = lerString(prompt);
            try {
                double value = Double.parseDouble(input);
                if (value < 0)
                    throw new NumberFormatException();
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número não-negativo.");
            }
        }
    }

    private static LocalDate lerData(String prompt, LocalDate defaultValue) {
        String input = lerString(prompt);
        if (input.isEmpty())
            return defaultValue;
        try {
            return LocalDate.parse(input, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Data inválida. Retornando valor padrão.");
            return defaultValue;
        }
    }

    private static LocalDateTime lerDataHora(String prompt, LocalDateTime defaultValue) {
        String input = lerString(prompt);
        if (input.isEmpty())
            return defaultValue;
        try {
            return LocalDateTime.parse(input, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            System.out.println("Data/hora inválida. Retornando valor padrão.");
            return defaultValue;
        }
    }

    private static List<Long> lerListaIds(String prompt) {
        String input = lerString(prompt);
        if (input.isEmpty())
            return new ArrayList<>();
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Long.parseLong(s);
                    } catch (NumberFormatException e) {
                        System.out.println("ID inválido ignorado: " + s);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}