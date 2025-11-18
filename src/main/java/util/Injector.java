package util;

import controller.UserMenuController;
import controller.admin.DeveloperAdminController;
import controller.admin.GameAdminController;
import controller.admin.GenreAdminController;
import controller.admin.PlatformAdminController;
import controller.admin.UserAdminController;
import service.AuthService;
import service.FriendshipService;
import service.GameService;
import service.GenreService;
import service.PlatformService;
import service.DeveloperService;
import service.UserGameService;
import service.UserService;

public final class Injector {
    private static final AuthService authService = new AuthService();
    private static final UserService userService = new UserService();
    private static final FriendshipService friendshipService = new FriendshipService();
    private static final UserGameService userGameService = new UserGameService();
    private static final GameService gameService = new GameService();
    private static final GenreService genreService = new GenreService();
    private static final PlatformService platformService = new PlatformService();
    private static final DeveloperService developerService = new DeveloperService();

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

    public static PlatformAdminController createPlatformAdminController() {
        return new  PlatformAdminController(
            platformService
        );
    }

    public static UserAdminController createUserAdminController() {
        return new UserAdminController(
            userService
        );
    }
}