package es.juanlsanchez.chefs.web.rest.dto;

import es.juanlsanchez.chefs.domain.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A DTO representing a user, with his authorities.
 */
public class MiniUserDTO {

    @Pattern(regexp = "^[a-z0-9]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    public MiniUserDTO() {
    }

    public MiniUserDTO(User user) {
        this(user.getLogin(), user.getFirstName(), user.getEmail());
    }

    public MiniUserDTO(String login, String firstName, String email) {
        this.login = login;
        this.firstName = firstName;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() { return email; }


    @Override
    public String toString() {
        return "MiniUserDTO{" +
        "login='" + login + '\'' +
        ", firstName='" + firstName + '\'' +
        ", email='" + email + '\'' +
        '}';
    }
}
