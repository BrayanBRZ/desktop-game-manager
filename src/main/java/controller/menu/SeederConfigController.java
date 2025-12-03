package controller.menu;

import service.exception.ServiceException;
import service.exception.ValidationException;
import view.SeederConfigView;
import core.Navigation;
import utils.ConsoleUtils;
import core.seed.DeveloperSeeder;
import core.seed.GameSeeder;
import core.seed.GenreSeeder;
import core.seed.PlatformSeeder;
import core.seed.UserSeeder;

public class SeederConfigController {

    private final GenreSeeder genreSeeder;
    private final PlatformSeeder platformSeeder;
    private final DeveloperSeeder developerSeeder;
    private final GameSeeder gameSeeder;
    private final UserSeeder userSeeder;

    public SeederConfigController(
            GenreSeeder genreSeeder,
            PlatformSeeder platformSeeder,
            DeveloperSeeder developerSeeder,
            GameSeeder gameSeeder,
            UserSeeder userSeeder
    ) {
        this.genreSeeder = genreSeeder;
        this.platformSeeder = platformSeeder;
        this.developerSeeder = developerSeeder;
        this.gameSeeder = gameSeeder;
        this.userSeeder = userSeeder;
    }

    private final SeederConfigView seederConfigView = new SeederConfigView();
    String[] options = {"", "Gêneros", "Plataformas", "Desenvolvedores", "Jogos", "Usuários", "Tudo"};

    public void seedersManagementMenu() {
        Navigation.push("Seeder Menu");

        while (true) {
            int choice = seederConfigView.renderBanner(
                    "1 - Semear Gêneros",
                    "2 - Semear Plataformas",
                    "3 - Semear Desenvolvedores",
                    "4 - Semear Jogos",
                    "5 - Semear Usuários (Faker)",
                    "6 - Semear Tudo (ordem correta)",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        runSeed(1, genreSeeder::seed);
                        break;
                    case 2:
                        runSeed(2, platformSeeder::seed);
                        break;
                    case 3:
                        runSeed(3, developerSeeder::seed);
                        break;
                    case 4:
                        runSeed(4, gameSeeder::seed);
                        break;
                    case 5:
                        // Lógica específica para Usuários (pede quantidade)
                        seederConfigView.renderMessageLine("[ SEEDER: USUÁRIOS ]");
                        int qtd = seederConfigView.readInteger("Quantidade de usuários a gerar: ");
                        userSeeder.seed(qtd);
                        seederConfigView.renderMessageLine("Usuários semeados com sucesso.");
                        ConsoleUtils.waitEnter();
                        break;
                    case 6:
                        seedAll();
                        break;
                    case 0:
                        Navigation.pop();
                        return;
                    default:
                        seederConfigView.renderError("Opção inválida.");
                        ConsoleUtils.waitEnter();
                }
            } catch (ValidationException e) {
                seederConfigView.renderValidationException(e);
                ConsoleUtils.waitEnter();
            } catch (ServiceException e) {
                seederConfigView.renderServiceException(e);
                ConsoleUtils.waitEnter();
            } catch (Exception e) {
                seederConfigView.renderException(e);
                ConsoleUtils.waitEnter();
            }
        }
    }

    private void runSeed(int optionIndex, Runnable seederAction) {
        seederConfigView.renderMessageLine("[ SEEDER: " + options[optionIndex].toUpperCase() + " ]");
        seederAction.run();
        seederConfigView.renderMessageLine(options[optionIndex] + " semeados com sucesso.");
        ConsoleUtils.waitEnter();
    }

    private void seedAll() throws Exception {
        seederConfigView.renderMessageLine("[ SEEDER: TODOS OS DADOS ]");
        
        genreSeeder.seed();
        platformSeeder.seed();
        developerSeeder.seed();
        
        gameSeeder.seed();
        
        userSeeder.seed(15);

        seederConfigView.renderMessageLine("TODOS os seeds foram executados com sucesso.");
        ConsoleUtils.waitEnter();
    }
}