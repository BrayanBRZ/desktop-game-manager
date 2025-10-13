package main;

import model.Developer;
import model.Game;
import model.Genre;
import model.Platform;
import service.*; // Importa todas as suas classes de serviço e exceções

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Instancia os serviços. Com a nova arquitetura, não precisamos mais do EntityManager aqui.
        GameService gameService = new GameService();
        GenreService genreService = new GenreService();
        PlatformService platformService = new PlatformService();
        DeveloperService developerService = new DeveloperService();

        Long newGameId;

        System.out.println("--- INICIANDO TESTE COMPLETO DO CRUD ---");
        try {
            // --- 1. SETUP: Criar dados de base ---
            System.out.println("\n[SETUP] Criando gêneros, plataformas e desenvolvedores...");
            Genre rpg = genreService.createOrFind("RPG");
            Genre action = genreService.createOrFind("Action");
            Platform pc = platformService.createOrFind("PC", "pc.png");
            Developer cdpr = developerService.createOrFind("CD Projekt Red");

            // --- 2. CREATE: Adicionar um novo jogo ---
            System.out.println("\n[CREATE] Adicionando 'Cyberpunk 2077'...");
            Game cyberpunk = gameService.createGame(
                    "Cyberpunk 2077",
                    LocalDate.of(2020, 12, 10),
                    Arrays.asList(rpg.getId(), action.getId()),
                    Collections.singletonList(pc.getId()),
                    Collections.singletonList(cdpr.getId())
            );
            newGameId = cyberpunk.getId();
            System.out.println("SUCESSO: Jogo criado com ID: " + newGameId);

            // --- 3. READ / SEARCH ---
            System.out.println("\n[READ] Buscando jogos...");
            List<Game> allGames = gameService.findAllGames();
            System.out.println("Total de jogos no banco: " + allGames.size());
            Game foundGame = gameService.findGameById(newGameId);
            System.out.println("Jogo encontrado pelo ID: " + foundGame.getName());

            // --- 4. UPDATE ---
            System.out.println("\n[UPDATE] Atualizando 'Cyberpunk 2077'...");
            Genre adventure = genreService.createOrFind("Adventure");
            Game updatedGame = gameService.updateGame(
                    newGameId,
                    "Cyberpunk 2077 - Phantom Liberty",
                    cyberpunk.getReleaseDate(),
                    Arrays.asList(rpg.getId(), action.getId(), adventure.getId()),
                    Collections.singletonList(pc.getId()),
                    Collections.singletonList(cdpr.getId())
            );
            System.out.println("SUCESSO: Nome do jogo atualizado para: '" + updatedGame.getName() + "'");
            
            // --- 5. DELETE ---
            System.out.println("\n[DELETE] Deletando o jogo com ID: " + newGameId);
            gameService.deleteGameById(newGameId);
            System.out.println("SUCESSO: Jogo deletado.");

            // --- 6. VERIFY DELETION ---
            System.out.println("\n[VERIFY] Verificando se o jogo foi deletado...");
            Game deletedGame = gameService.findGameById(newGameId);
            if (deletedGame == null) {
                System.out.println("VERIFICAÇÃO OK: O jogo com ID " + newGameId + " não foi encontrado.");
            } else {
                System.err.println("VERIFICAÇÃO FALHOU: O jogo ainda existe.");
            }

        // Este bloco 'catch' é o "plano" para lidar com erros de validação.
        } catch (ValidationException e) {
            System.err.println("!!! ERRO DE VALIDAÇÃO: " + e.getMessage());

        // Este bloco 'catch' é o "plano" para lidar com erros de sistema (ex: banco de dados).
        } catch (ServiceException e) {
            System.err.println("!!! ERRO DE SERVIÇO: " + e.getMessage());
        }
    }
}