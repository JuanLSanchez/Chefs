package es.juanlsanchez.chefs.web.rest.dto;

import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO representing a user, with his authorities.
 */
public class SearchDTO {

    @NotNull
    @Size(min = 1, max = 50)
    private String firstField;

    @NotNull
    @Size(min = 1, max = 50)
    private String login;

    @Size(max = 50)
    private String secondField;

    @Size(max = 50)
    private String type;

    public SearchDTO() {
    }

    public SearchDTO(User user) {
        this(user.getLogin(), user.getFirstName(), "user", user.getLogin());
    }

    public SearchDTO(Recipe recipe) {
        this(recipe.getId().toString(), recipe.getName(), "recipe", recipe.getUser().getLogin());
    }

    public SearchDTO(String firstField, String secondField, String type, String login) {
        this.firstField = firstField;
        this.secondField = secondField;
        this.type = type;
        this.login = login;
    }

    public String getFirstField() { return firstField; }

    public String getSecondField() { return secondField; }

    public String getType() { return type; }

    public String getLogin() { return login; }

    @Override
    public String toString() {
        return "SearchDTO{" +
        "firstField='" + firstField + '\'' +
        ", secondField='" + secondField + '\'' +
        ", type='" + type + '\'' +
        ", login='" + login + '\'' +
        '}';
    }
}
