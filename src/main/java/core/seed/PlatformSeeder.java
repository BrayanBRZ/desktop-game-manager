package core.seed;

import service.game.PlatformService;

public class PlatformSeeder {

    private final PlatformService platformService;

    public PlatformSeeder(PlatformService platformService) {
        this.platformService = platformService;
    }

    public void seed() {
        String[] platforms = {
            "PC",
            "PlayStation 5",
            "PlayStation 4",
            "PlayStation 3",
            "Xbox Series X",
            "Xbox Series S",
            "Xbox One",
            "Xbox 360",
            "Nintendo Switch",
            "Nintendo Wii U",
            "Nintendo 3DS",
            "Nintendo DS",
            "Steam Deck",
            "Android",
            "iOS",
            "VR Headset",
            "PlayStation Vita",
            "Google Stadia",
            "Amazon Luna",
            "MacOS"
        };

        for (String p : platforms) {
            platformService.createPlatform(p);
        }
    }
}
