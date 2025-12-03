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

public class SeederConfigController {

    private final GenreSeeder genreSeeder;
    private final PlatformSeeder platformSeeder;
    private final DeveloperSeeder developerSeeder;
    private final GameSeeder gameSeeder;

    public SeederConfigController(
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

    private final SeederConfigView seederConfigView = new SeederConfigView();
    String[] options = {"", "Gêneros", "Plataformas", "Desenvolvedores", "Jogos", "Todos"};

    public void seedersManagementMenu() {
        Navigation.push("Seeder Menu");

        while (true) {
            int choice = seederConfigView.renderBanner(
                    "1 - Semear Gêneros",
                    "2 - Semear Plataformas",
                    "3 - Semear Desenvolvedores",
                    "4 - Semear Jogos",
                    "5 - Semear Tudo (ordem correta)",
                    "0 - Voltar"
            );

            try {
                switch (choice) {
                    case 1:
                        seederConfigView.renderMessage("[ SEEDER: " + options[choice].toUpperCase() + " ]");
                        genreSeeder.seed();
                        seederConfigView.renderMessage(options[choice] + " semeados com sucesso.");
                        ConsoleUtils.waitEnter();
                        break;
                    case 2:
                        seederConfigView.renderMessage("[ SEEDER: " + options[choice].toUpperCase() + " ]");
                        platformSeeder.seed();
                        seederConfigView.renderMessage(options[choice] + " semeados com sucesso.");
                        ConsoleUtils.waitEnter();
                        break;
                    case 3:
                        seederConfigView.renderMessage("[ SEEDER: " + options[choice].toUpperCase() + " ]");
                        developerSeeder.seed();
                        seederConfigView.renderMessage(options[choice] + " semeados com sucesso.");
                        ConsoleUtils.waitEnter();
                        break;
                    case 4:
                        seederConfigView.renderMessage("[ SEEDER: " + options[choice].toUpperCase() + " ]");
                        gameSeeder.seed();
                        seederConfigView.renderMessage(options[choice] + " semeados com sucesso.");
                        ConsoleUtils.waitEnter();
                        break;
                    case 5:
                        seederConfigView.renderMessage("[ SEEDER: " + options[choice].toUpperCase() + " ]");
                        genreSeeder.seed();
                        platformSeeder.seed();
                        developerSeeder.seed();
                        gameSeeder.seed();
                        seederConfigView.renderMessage(options[choice] + " semeados com sucesso.");
                        ConsoleUtils.waitEnter();
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
}
