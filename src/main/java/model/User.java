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

    @Column(name = "username", nullable = false, length = 150)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "avatar_path")
    private Long avatarPath;

    @Column(name = "birth_path")
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
    public void addGame(Game game) {
        boolean alreadyHas = userGames.stream()
                .anyMatch(ug -> ug.getGame().equals(game));

        if (!alreadyHas) {
            UserGame ug = new UserGame(this, game);
            userGames.add(ug);
        }
    }

    public void removeGame(Game game) {
        UserGame toRemove = userGames.stream()
                .filter(ug -> ug.getGame().equals(game))
                .findFirst()
                .orElse(null);

        if (toRemove != null) {
            userGames.remove(toRemove);
            toRemove.setUser(null);
        }
    }
    // #endregion

    // #region Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
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

    public Long getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(Long avatarPath) {
        this.avatarPath = avatarPath;
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
