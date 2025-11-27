package controller.menu;

import core.Navigation;
import core.seed.DeveloperSeeder;
import core.seed.GameSeeder;
import core.seed.GenreSeeder;
import core.seed.PlatformSeeder;
import service.exception.ServiceException;
import service.exception.ValidationException;
import util.ConsoleUtils;
import view.MenuRenderer;

public class SeederAdminController {

    private final GenreSeeder genreSeeder;
    private final PlatformSeeder platformSeeder;
    private final DeveloperSeeder developerSeeder;
    private final GameSeeder gameSeeder;

    public SeederAdminController(
            GenreSeeder genreSeeder,
            PlatformSeeder platformSeeder,
            DeveloperSeeder developerSeeder,
            GameSeeder gameSeeder
    ) {
        this.genreSeeder = genreSeeder;
        this.platformSeeder = platformSeeder;
        this.developerSeeder = developerSeeder;
        this.gameSeeder = gameSeeder;
    }

    public void manageSeedersMenu() {
        Navigation.push("Seeder Menu");

        while (true) {
            ConsoleUtils.clearScreen();
            MenuRenderer.renderBanner(Navigation.getPath());
            MenuRenderer.renderOptions(
                    "1 - Semear Gêneros",
                    "2 - Semear Plataformas",
                    "3 - Semear Desenvolvedores",
                    "4 - Semear Jogos",
                    "5 - Semear Tudo (ordem correta)",
                    "0 - Voltar"
            );

            String option = ConsoleUtils.readString("Escolha: ");

            try {
                switch (option) {
                    case "1":
                        seedGenres();
                        ConsoleUtils.waitEnter();
                        break;
                    case "2":
                        seedPlatforms();
                        ConsoleUtils.waitEnter();
                        break;
                    case "3":
                        seedDevelopers();
                        ConsoleUtils.waitEnter();
                        break;
                    case "4":
                        seedGames();
                        ConsoleUtils.waitEnter();
                        break;
                    case "5":
                        seedAll();
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

    private void seedGenres() throws ServiceException, ValidationException {
        System.out.println("\n[ SEEDER: GÊNEROS ]");
        genreSeeder.seed();
        System.out.println("Gêneros semeados com sucesso.");
    }

    private void seedPlatforms() throws ServiceException, ValidationException {
        System.out.println("\n[ SEEDER: PLATAFORMAS ]");
        platformSeeder.seed();
        System.out.println("Plataformas semeadas com sucesso.");
    }

    private void seedDevelopers() throws ServiceException, ValidationException {
        System.out.println("\n[ SEEDER: DESENVOLVEDORES ]");
        developerSeeder.seed();
        System.out.println("Desenvolvedores semeados com sucesso.");
    }

    private void seedGames() throws ServiceException, ValidationException {
        System.out.println("\n[ SEEDER: GAMES ]");
        gameSeeder.seed();
        System.out.println("Jogos semeados com sucesso.");
    }

    private void seedAll() throws ServiceException, ValidationException {
        System.out.println("\n[ SEEDER: TODOS OS DADOS ]");

        genreSeeder.seed();
        platformSeeder.seed();
        developerSeeder.seed();
        gameSeeder.seed();

        System.out.println("\nTODOS OS SEEDS FORAM EXECUTADOS COM SUCESSO!");
    }
}
