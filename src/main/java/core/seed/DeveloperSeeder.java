package core.seed;

import service.game.DeveloperService;

public class DeveloperSeeder {

    private final DeveloperService developerService;

    public DeveloperSeeder() {
        this.developerService = new DeveloperService();
    }

    public void seed() {
        String[] developers = {
            "Nintendo", "Valve", "CD Projekt Red", "Rockstar Games",
            "FromSoftware", "Ubisoft", "Bethesda", "Square Enix",
            "Blizzard Entertainment", "Capcom"
        };

        for (String d : developers) {
            developerService.createDeveloper(d);
        }
    }
}
