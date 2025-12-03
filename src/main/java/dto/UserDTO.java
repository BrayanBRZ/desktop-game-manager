package dto;

import java.time.LocalDate;

public class UserDTO {

    private final Long id;
    private final String name;
    private String password;
    private final LocalDate birthDate;

    public UserDTO(
            Long id,
            String name,
            String password,
            LocalDate birthDate
    ) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.birthDate = birthDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

}
