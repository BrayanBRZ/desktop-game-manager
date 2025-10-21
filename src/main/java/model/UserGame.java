package model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
        name = "user_game",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "game_id"})
        }
)
public class UserGame {

    //#region Private Fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_path")
    private Long gamePath;

    private boolean estimated;

    private UserGameState gameState;

    @Column(name = "total_time_played")
    private double totaltimePlayed;

    @Column(name = "last_time_played")
    private LocalDateTime lastTimePlayed;

    // --- Relationships ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "game_id")
    private Game game;

    // --- Audit Fields ---
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    //#endregion

    // #region Constructors
    public UserGame() {
    }

    public UserGame(User user, Game game) {
        this.user = user;
        this.game = game;
    }
    //#endregion

    //#region Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGamePath() {
        return gamePath;
    }

    public void setGamePath(Long gamePath) {
        this.gamePath = gamePath;
    }

    public boolean isEstimated() {
        return estimated;
    }

    public void setEstimated(boolean estimated) {
        this.estimated = estimated;
    }

    public UserGameState getGameState() {
        return gameState;
    }

    public void setGameState(UserGameState gameState) {
        this.gameState = gameState;
    }

    public double getTotaltimePlayed() {
        return totaltimePlayed;
    }

    public void setTotaltimePlayed(double totaltimePlayed) {
        this.totaltimePlayed = totaltimePlayed;
    }

    public LocalDateTime getLastTimePlayed() {
        return lastTimePlayed;
    }

    public void setLastTimePlayed(LocalDateTime lastTimePlayed) {
        this.lastTimePlayed = lastTimePlayed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
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
