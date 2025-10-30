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
@Table(name = "games")
public class Game {

    // #region Private Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = true, name = "release_date")
    private LocalDate releaseDate;

    @Column(nullable = true, length = 500, name = "cover_path")
    private String coverPath;

    @Column(nullable = true, length = 500, name = "banner_path")
    private String bannerPath;

    @Column(nullable = true, precision = 3, scale = 1)
    private Double rating;

    // --- Relationships ---
    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<GameGenre> gameGenres = new HashSet<>();

    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<GamePlatform> gamePlatforms = new HashSet<>();

    @OneToMany(
            mappedBy = "game",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Set<GameDeveloper> gameDevelopers = new HashSet<>();

    // --- Audit Fields ---
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // #endregion Private Fields

    //#region Constructors
    public Game() {
    }

    public Game(String name, LocalDate releaseDate, String coverPath, String bannerPath, Double rating) {
        this.name = name;
        this.releaseDate = releaseDate;
        this.coverPath = coverPath;
        this.bannerPath = bannerPath;
        this.rating = rating;
    }
    //#endregion Constructors

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

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getBannerPath() {
        return bannerPath;
    }

    public void setBannerPath(String bannerPath) {
        this.bannerPath = bannerPath;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Set<GameGenre> getGameGenres() {
        return gameGenres;
    }

    public void setGameGenres(Set<GameGenre> gameGenres) {
        this.gameGenres = gameGenres;
    }

    public Set<GamePlatform> getGamePlatforms() {
        return gamePlatforms;
    }

    public void setGamePlatforms(Set<GamePlatform> gamePlatforms) {
        this.gamePlatforms = gamePlatforms;
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
    // #endregion Getters and Setters
}
