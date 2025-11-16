
import controller.MainMenuController;

public class GameApp {

    public static void main(String[] args) {
        System.out.println("=== Game Desktop Manager ===");
        new MainMenuController().runMainMenu();
        System.out.println("Aplicação encerrada.");
    }
}
