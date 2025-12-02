package view;

import utils.ConsoleUtils;
import utils.MenuRenderer;

public class MainMenuView extends BaseView {

    public String mainMenu() {
        ConsoleUtils.clearScreen();
        MenuRenderer.renderBanner("Desktop Game Manager (Home)");
        MenuRenderer.renderOptions(
            "1 - Efetuar (Login/Registro)",
            "2 - Gerenciar Configurações",
            "0 - Sair"
        );
        return ConsoleUtils.readString("Escolha: ").trim();
    }
}