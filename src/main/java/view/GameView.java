package view;

import model.Game;
import service.GameService;
import service.ServiceException;

import util.ConsoleUtils;

import java.util.List;
import java.util.stream.Collectors;

public class GameView {

    private static final GameService gameService = new GameService();

    public static void listarTodosJogos() throws ServiceException {
        List<Game> jogos = gameService.findAll();
        System.out.println("\n--- TODOS OS JOGOS (" + jogos.size() + ") ---");
        if (jogos.isEmpty()) {
            System.out.println("Nenhum jogo cadastrado.");
        } else {
            jogos.forEach(GameView::exibirJogoResumido);
        }
    }

    public static void buscarJogoPorNome() throws ServiceException {
        String term = ConsoleUtils.readString("Termo de busca: ");
        List<Game> jogos = gameService.findByNameContaining(term);
        exibirListaJogos(jogos, "Nenhum jogo encontrado com '" + term + "'.");
    }

    public static void listarPorGenero() throws ServiceException {
        GenreView.listarGeneros();
        Long genre = ConsoleUtils.readLong("ID do gênero: ");
        List<Game> jogos = gameService.listByGenreId(genre);
        exibirListaJogos(jogos, "Nenhum jogo encontrado para o gênero '" + genre + "'.");
    }

    public static void listarPorPlataforma() throws ServiceException {
        PlatformView.listarPlataformas();
        Long platform = ConsoleUtils.readLong("ID da plataforma: ");
        List<Game> jogos = gameService.listByPlatformId(platform);
        exibirListaJogos(jogos, "Nenhum jogo encontrado para a plataforma '" + platform + "'.");
    }

    public static void listarPorDesenvolvedor() throws ServiceException {
        DeveloperView.listarDesenvolvedores();
        Long dev = ConsoleUtils.readLong("ID do desenvolvedor: ");
        List<Game> jogos = gameService.listByDeveloperId(dev);
        exibirListaJogos(jogos, "Nenhum jogo encontrado para o desenvolvedor:'" + dev + "'.");
    }

    public static void exibirListaJogos(List<Game> jogos, String emptyMessage) {
        if (jogos.isEmpty()) {
            System.out.println(emptyMessage);
        } else {
            jogos.forEach(GameView::exibirJogoResumido);
        }
    }

        // --- EXIBIÇÃO DE JOGOS ---
    public static void exibirJogoResumido(Game jogo) {
        String generos = jogo.getGameGenres().stream().map(gg -> gg.getGenre().getName())
                .collect(Collectors.joining(", "));
        String plataformas = jogo.getGamePlatforms().stream().map(gp -> gp.getPlatform().getName())
                .collect(Collectors.joining(", "));
        String desenvolvedores = jogo.getGameDevelopers().stream().map(gd -> gd.getDeveloper().getName())
                .collect(Collectors.joining(", "));
        System.out.printf("ID: %d | %s (%s) | Gêneros: [%s] | Plataformas: [%s] | Devs: [%s]%n",
                jogo.getId(), jogo.getName(),
                jogo.getReleaseDate() != null ? ConsoleUtils.formatDate(jogo.getReleaseDate()) : "N/A",
                generos.isEmpty() ? "Nenhum" : generos,
                plataformas.isEmpty() ? "Nenhum" : plataformas,
                desenvolvedores.isEmpty() ? "Nenhum" : desenvolvedores);
    }

    public static void exibirJogoCompleto(Game jogo) {
        System.out.println("\n=== DETALHES DO JOGO ===");
        System.out.println("ID: " + jogo.getId());
        System.out.println("Nome: " + jogo.getName());
        System.out.println("Lançamento: "
                + ConsoleUtils.formatDate(jogo.getReleaseDate()));
        System.out.println("Rating: "
                + (jogo.getRating() != null ? jogo.getRating() : "N/A"));

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
}