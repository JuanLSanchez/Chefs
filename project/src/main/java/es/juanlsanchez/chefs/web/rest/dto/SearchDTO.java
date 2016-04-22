package es.juanlsanchez.chefs.web.rest.dto;

import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Tag;
import es.juanlsanchez.chefs.domain.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO representing a user, with his authorities.
 */
public class SearchDTO {

    public static final String TYPE_USER = "user";
    public static final String TYPE_RECIPE = "recipe";
    private static final String TYPE_TAG = "tag";

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

    public SearchDTO(User user) {
        this(user.getLogin(), user.getFirstName(), TYPE_USER, user.getLogin());
    }

    public SearchDTO(Recipe recipe) {
        this(recipe.getId().toString(), recipe.getName(), TYPE_RECIPE, recipe.getUser().getLogin());
    }

    public SearchDTO(Tag tag) {
        this(tag.getId().toString(), tag.getName(), TYPE_TAG, tag.getName());
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
