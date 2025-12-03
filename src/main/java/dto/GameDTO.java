package dto;

import java.time.LocalDate;
import utils.MyLinkedList;

public class GameDTO {

    private Long id;
    private String name;
    private LocalDate releaseDate;
    private MyLinkedList<Long> genreIds;
    private MyLinkedList<Long> platformIds;
    private MyLinkedList<Long> developerIds;

    public GameDTO() {}

    public GameDTO(
            Long id,
            String name,
            LocalDate releaseDate,
            MyLinkedList<Long> genreIds,
            MyLinkedList<Long> platformIds,
            MyLinkedList<Long> developerIds
    ) {
        this.id = id;
        this.name = name;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
        this.platformIds = platformIds;
        this.developerIds = developerIds;
    }

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

    public MyLinkedList<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(MyLinkedList<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public MyLinkedList<Long> getPlatformIds() {
        return platformIds;
    }

    public void setPlatformIds(MyLinkedList<Long> platformIds) {
        this.platformIds = platformIds;
    }

    public MyLinkedList<Long> getDeveloperIds() {
        return developerIds;
    }

    public void setDeveloperIds(MyLinkedList<Long> developerIds) {
        this.developerIds = developerIds;
    }
}
