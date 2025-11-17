package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 250)
    private String password;

    @Column(nullable = true, length = 500, name = "avatar_path")
    private String avatarPath;

    @Column(nullable = true, name = "birth_date")
    private LocalDate birthDate;

    // --- Relationships ---
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private Set<UserGame> userGames = new HashSet<>();

    @OneToMany(
        mappedBy = "fromUser",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER)
    private Set<FriendRequest> sentRequests = new HashSet<>();

    @OneToMany(
        mappedBy = "toUser",
        fetch = FetchType.EAGER)
    private Set<FriendRequest> receivedRequests = new HashSet<>();

    // --- Helpers Methods ---
    public Set<User> getFriends() {
        Set<User> friends = new HashSet<>();
        sentRequests.stream()
            .filter(r -> r.getStatus() == FriendRequestState.ACCEPTED)
            .map(FriendRequest::getToUser)
            .forEach(friends::add);
        receivedRequests.stream()
            .filter(r -> r.getStatus() == FriendRequestState.ACCEPTED)
            .map(FriendRequest::getFromUser)
            .forEach(friends::add);
        return friends;
    }

    public Set<FriendRequest> getPendingRequests() {
        return receivedRequests.stream()
            .filter(r -> r.getStatus() == FriendRequestState.PENDING)
            .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
            .collect(Collectors.toSet());
    }

    public Set<FriendRequest> getRejectedRequests() {
        return receivedRequests.stream()
            .filter(r -> r.getStatus() == FriendRequestState.REJECTED)
            .sorted(Comparator.comparing(FriendRequest::getCreatedAt).reversed())
            .collect(Collectors.toSet());
    }

    // --- Audit Fields ---
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // #endregion Private Fields

    // #region Constructors
    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public User(String name, String password, String avatarPath, LocalDate birthDate) {
        this.name = name;
        this.password = password;
        this.avatarPath = avatarPath;
        this.birthDate = birthDate;
    }
    // #endregion Constructors

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

    public Set<FriendRequest> getSentRequests() {
        return sentRequests;
    }

    public void setSentRequests(Set<FriendRequest> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public Set<FriendRequest> getReceivedRequests() {
        return receivedRequests;
    }

    public void setReceivedRequests(Set<FriendRequest> receivedRequests) {
        this.receivedRequests = receivedRequests;
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
