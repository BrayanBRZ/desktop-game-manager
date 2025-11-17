package session;

import model.User;

public class SessionManager {
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    public static void login(User user) {
        currentUser.set(user);
    }

    public static User getCurrentUser() {
        User user = currentUser.get();
        if (user == null) {
            throw new IllegalStateException("Nenhum usuário logado na sessão atual.");
        }
        return user;
    }

    public static Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public static void logout() {
        currentUser.remove();
    }

    public static boolean isLoggedIn() {
        return currentUser.get() != null;
    }
}
