package es.juanlsanchez.chefs.web.rest.dto;

import es.juanlsanchez.chefs.domain.Authority;
import es.juanlsanchez.chefs.domain.User;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 100;

    @Pattern(regexp = "^[a-z0-9]*$")
    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    @NotNull
    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 255)
    private String biography;

    @Email
    @Size(min = 5, max = 100)
    private String email;

    private boolean activated = false;

    @Size(min = 2, max = 5)
    private String langKey;

    private Set<String> authorities;

    private byte[] profilePicture;

    private byte[] backgroundPicture;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this(user.getLogin(), null, user.getFirstName(), user.getBiography(),
            user.getEmail(), user.getActivated(), user.getLangKey(),
            user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()),
            user.getProfilePicture()==null?null:user.getProfilePicture().getSrc(),
            user.getBackgroundPicture()==null?null:user.getBackgroundPicture().getSrc());
    }

    public UserDTO(String login, String password, String firstName, String biography,
            String email, boolean activated, String langKey, Set<String> authorities,
            byte[] profilePicture, byte[] backgroundPicture) {

        this.login = login;
        this.password = password;
        this.firstName = firstName;
        this.biography = biography;
        this.email = email;
        this.activated = activated;
        this.langKey = langKey;
        this.authorities = authorities;
        this.profilePicture = profilePicture;
        this.backgroundPicture = backgroundPicture;
    }

    public UserDTO(String login, String firstName, String biography, byte[] profilePicture, byte[] backgroundPicture, String email) {
        this.login = login;
        this.firstName = firstName;
        this.biography = biography;
        this.profilePicture = profilePicture;
        this.backgroundPicture = backgroundPicture;
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getBiography() {
        return biography;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getLangKey() {
        return langKey;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public byte[] getProfilePicture() { return profilePicture; }

    public byte[] getBackgroundPicture() { return backgroundPicture; }

    @Override
    public String toString() {
        return "UserDTO{" +
        "login='" + login + '\'' +
        ", password='" + password + '\'' +
        ", firstName='" + firstName + '\'' +
        ", biography='" + biography + '\'' +
        ", email='" + email + '\'' +
        ", activated=" + activated +
        ", langKey='" + langKey + '\'' +
        ", authorities=" + authorities +
        '}';
    }
}
