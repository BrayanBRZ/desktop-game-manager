package model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
@Table(name = "developers")
public class Developer {

    // #region Private Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "symbol_path")
    private String symbolPath;

    @Column(nullable = false)
    private Long location;

    // --- Relationships ---
    @OneToMany(
            mappedBy = "developer",
            fetch = FetchType.LAZY
    )
    private Set<GameDeveloper> gameDevelopers = new HashSet<>();

    // --- Audit Fields ---
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // #endregion

    // #region Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbolPath() {
        return symbolPath;
    }

    public void setSymbolPath(String symbolPath) {
        this.symbolPath = symbolPath;
    }

    public Long getLocation() {
        return location;
    }

    public void setLocation(Long location) {
        this.location = location;
    }

    public Set<GameDeveloper> getGameDevelopers() {
        return gameDevelopers;
    }

    public void setGameDevelopers(Set<GameDeveloper> gameDevelopers) {
        this.gameDevelopers = gameDevelopers;
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
