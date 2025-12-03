package core;

import controller.game.DeveloperConfigController;
import controller.game.GameConfigController;
import controller.game.GenreConfigController;
import controller.game.PlatformConfigController;
import controller.menu.MainConfigController;
import controller.menu.SeederConfigController;
import controller.user.FriendMenuController;
import controller.user.UserConfigController;
import controller.user.UserMenuController;
import core.seed.DeveloperSeeder;
import core.seed.GameSeeder;
import core.seed.GenreSeeder;
import core.seed.PlatformSeeder;
import core.seed.UserSeeder;
import service.game.DeveloperService;
import service.game.GameService;
import service.game.GenreService;
import service.game.PlatformService;
import service.session.AuthService;
import service.user.FriendshipService;
import service.user.UserGameService;
import service.user.UserService;

public final class Injector {

    private static final AuthService authService = new AuthService();
    private static final UserService userService = new UserService();
    private static final FriendshipService friendshipService = new FriendshipService();
    private static final UserGameService userGameService = new UserGameService();
    private static final GameService gameService = new GameService();
    private static final GenreService genreService = new GenreService();
    private static final PlatformService platformService = new PlatformService();
    private static final DeveloperService developerService = new DeveloperService();
    private static final GenreSeeder genreSeeder = new GenreSeeder(genreService);
    private static final PlatformSeeder platformSeeder = new PlatformSeeder(platformService);
    private static final DeveloperSeeder developerSeeder = new DeveloperSeeder(developerService);

    private static final GameSeeder gameSeeder = new GameSeeder(
            gameService
    );

    private static final UserSeeder userSeeder = new UserSeeder(
            authService,
            userService,
            userGameService,
            friendshipService,
            gameService
    );

    public static UserMenuController createUserMenuController() {
        return new UserMenuController(
                authService,
                userService,
                friendshipService,
                userGameService
        );
    }

    public static DeveloperConfigController createDeveloperConfigController() {
        return new DeveloperConfigController(developerService);
    }

    public static GameConfigController createGameConfigController() {
        return new GameConfigController(
                gameService,
                genreService,
                platformService,
                developerService
        );
    }

    public static GenreConfigController createGenreConfigController() {
        return new GenreConfigController(genreService);
    }

    public static PlatformConfigController createPlatformMenuController() {
        return new PlatformConfigController(platformService);
    }

    public static UserConfigController createUserConfigController() {
        return new UserConfigController(
                userService,
                authService
        );
    }

    public static FriendMenuController createFriendMenuController() {
        return new FriendMenuController(
                userService,
                friendshipService
        );
    }

    public static MainConfigController createConfigMenuController() {
        return new MainConfigController();
    }

    public static SeederConfigController createSeederConfigController() {
        return new SeederConfigController(
                genreSeeder,
                platformSeeder,
                developerSeeder,
                gameSeeder,
                userSeeder
        );
    }
}
