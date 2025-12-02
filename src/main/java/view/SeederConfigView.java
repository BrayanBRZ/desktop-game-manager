package view;

public class SeederConfigView extends BaseView {

    public void renderInitialMessage(String msg) {
        System.out.println("\n[ SEEDER: " + msg.toUpperCase() + " ]");
    }

    public void renderSuccessMessage(String msg) {
        System.out.println(msg + " semeados com sucesso.");
    }
}
