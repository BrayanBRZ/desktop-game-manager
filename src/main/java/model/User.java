package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "users")
public class User {

    // #region Private Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 150)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "avatar_path")
    private String avatarPath;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    // --- Relationships ---
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserGame> userGames = new HashSet<>();

    // --- Audit Fields ---
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // #endregion

    // #region Constructors
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    // #endregion

    // #region Owner Methods
    /**
     * Adds a game to the user's library, ensuring the bidirectional
     * relationship is maintained. Prevents adding a game if it's already in the
     * library.
     *
     * @param game The Game to add.
     */
    public void addGame(Game game) {
        if (game == null) {
            return;
        }
        boolean alreadyHas = this.userGames.stream()
                .anyMatch(ug -> ug.getGame() != null && ug.getGame().equals(game));

        if (!alreadyHas) {
            UserGame userGame = new UserGame(this, game);
            this.userGames.add(userGame);
        }
    }

    /**
     * Removes a game from the user's library, ensuring the bidirectional
     * relationship is broken.
     *
     * @param game The Game to remove.
     */
    public void removeGame(Game game) {
        if (game == null) {
            return;
        }

        this.userGames.removeIf(userGame -> {
            if (userGame.getGame() != null && userGame.getGame().equals(game)) {
                userGame.setUser(null);
                userGame.setGame(null);
                return true;
            }
            return false;
        });
    }
    // #endregion

    // #region Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public String setAvatarPath(String avatarPath) {
        return this.avatarPath = avatarPath;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Set<UserGame> getUserGames() {
        return userGames;
    }

    public void setUserGames(Set<UserGame> userGames) {
        this.userGames = userGames;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    // #endregion
}
