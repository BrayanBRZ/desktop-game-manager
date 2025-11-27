package core.seed;

import service.game.PlatformService;

public class PlatformSeeder {

    private final PlatformService platformService;

    public PlatformSeeder() {
        this.platformService = new PlatformService();
    }

    public void seed() {
        String[] platforms = {
            "PC", "PlayStation 5", "PlayStation 4", "Xbox Series X", "Xbox One",
            "Nintendo Switch", "Android", "iOS", "Steam Deck", "VR Headset"
        };

        for (String p : platforms) {
            platformService.createPlatform(p);
        }
    }
}
