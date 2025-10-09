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

    @Column(name = "display_name", nullable = false, length = 150)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "avatar_path")
    private Long avatarPath;

    @Column(name = "birth_path", nullable = false)
    private LocalDate birthDate;

    // --- Relationships ---
    @OneToMany(
            mappedBy = "user", // "user" é o nome do campo na classe UserGame que aponta de volta para esta classe.
            cascade = CascadeType.ALL, // Salva/atualiza/deleta os UserGames junto com o User.
            orphanRemoval = true, // Remove um UserGame do banco se ele for removido desta coleção.
            fetch = FetchType.LAZY // Carrega a biblioteca apenas quando for explicitamente acessada.
    )
    private Set<UserGame> userGames = new HashSet<>();

    // --- Audit Fields ---
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    //#endregion

    //#region Getters and Setters
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
    //#endregion
}
