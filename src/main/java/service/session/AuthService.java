// service/AuthService.java
package service.session;

import model.user.User;
import dao.user.UserDAO;
import service.exception.ServiceException;
import service.exception.ValidationException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    private String hashPassword(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 não disponível na JVM", e);
        }
    }

    public User register(String name, String password) throws ValidationException {
        if (name == null || name.trim().length() < 3) {
            throw new ValidationException("Nome inválido");
        }
        if (password == null || password.length() < 6) {
            throw new ValidationException("Senha deve ter pelo menos 6 caracteres");
        }
        if (userDAO.findByName(name.trim()) != null) {
            throw new ValidationException("Nome já existe");
        }

        User user = new User(name.trim(), hashPassword(password));
        userDAO.save(user);
        return sanitize(user);
    }

    public User login(String name, String password) throws ServiceException {
        User user = userDAO.findByName(name.trim());
        if (user == null || !hashPassword(password).equals(user.getPassword())) {
            return null;
        }
        return sanitize(user);
    }

    public void changePassword(Long userId, String current, String nova) throws ValidationException {
        User user = userDAO.findById(userId);
        if (!hashPassword(current).equals(user.getPassword())) {
            throw new ValidationException("Senha atual incorreta");
        }
        if (nova.length() < 6) {
            throw new ValidationException("Nova senha muito curta");
        }
        user.setPassword(hashPassword(nova));
        userDAO.update(user);
    }

    private User sanitize(User u) {
        User safe = new User();
        safe.setId(u.getId());
        safe.setName(u.getName());
        safe.setBirthDate(u.getBirthDate());
        safe.setCreatedAt(u.getCreatedAt());
        return safe;
    }
}
