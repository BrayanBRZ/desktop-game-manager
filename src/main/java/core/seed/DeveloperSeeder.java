package core.seed;

import service.game.DeveloperService;

public class DeveloperSeeder {

    private final DeveloperService developerService;

    public DeveloperSeeder(DeveloperService developerService) {
        this.developerService = developerService;
    }

    public void seed() {
        String[] developers = {
            "Nintendo",
            "Valve",
            "CD Projekt Red",
            "Rockstar Games",
            "FromSoftware",
            "Ubisoft",
            "Bethesda",
            "Square Enix",
            "Blizzard Entertainment",
            "Capcom",
            "Sega",
            "Bandai Namco",
            "Konami",
            "Insomniac Games",
            "Naughty Dog",
            "EA Games",
            "Epic Games",
            "BioWare",
            "Rare",
            "id Software"
        };

        for (String d : developers) {
            developerService.createDeveloper(d);
        }
    }
}
