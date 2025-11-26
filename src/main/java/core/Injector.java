package core;

import controller.game.DeveloperAdminController;
import controller.game.GameAdminController;
import controller.game.GenreAdminController;
import controller.game.PlatformAdminController;
import controller.menu.ConfigAdminController;
import controller.menu.SeederAdminController;
import controller.user.FriendMenuController;
import controller.user.UserAdminController;
import controller.user.UserMenuController;
import core.seed.DeveloperSeeder;
import core.seed.GameSeeder;
import core.seed.GenreSeeder;
import core.seed.PlatformSeeder;
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
    private static final GenreSeeder genreSeeder = new GenreSeeder();
    private static final PlatformSeeder platformSeeder = new PlatformSeeder();
    private static final DeveloperSeeder developerSeeder = new DeveloperSeeder();
    private static final GameSeeder gameSeeder = new GameSeeder();
    
    

    public static UserMenuController createUserMenuController() {
        return new UserMenuController(
            authService,
            userService,
            friendshipService,
            userGameService
        );
    }

    public static DeveloperAdminController createDeveloperAdminController() {
        return new DeveloperAdminController(
            developerService
        );
    }

    public static GameAdminController createGameAdminController() {
        return new GameAdminController(
            gameService,
            genreService,
            platformService,
            developerService
        );
    }

    public static GenreAdminController createGenreAdminController() {
        return new GenreAdminController(
            genreService
        );
    }

    public static PlatformAdminController createPlatformMenuController() {
        return new  PlatformAdminController(
            platformService
        );
    }

    public static UserAdminController createUserAdminController() {
        return new UserAdminController(
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

    public static ConfigAdminController createConfigMenuController() {
        return new ConfigAdminController();
    }

    public static SeederAdminController createSeederAdminController() {
        return new SeederAdminController(
            genreSeeder,
            platformSeeder,
            developerSeeder,
            gameSeeder
        );
    }
}