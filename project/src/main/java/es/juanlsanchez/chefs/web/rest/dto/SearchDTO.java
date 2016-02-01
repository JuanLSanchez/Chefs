package es.juanlsanchez.chefs.web.rest.dto;

import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.User;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A DTO representing a user, with his authorities.
 */
public class SearchDTO {

    @NotNull
    @Size(min = 1, max = 50)
    private String firstField;

    @Size(max = 50)
    private String secondField;

    @Size(max = 50)
    private String type;

    public SearchDTO() {
    }

    public SearchDTO(User user) {
        this(user.getLogin(), user.getFirstName(), "user");
    }

    public SearchDTO(Recipe recipe) {
        this(recipe.getId().toString(), recipe.getName(), "recipe");
    }

    public SearchDTO(String firstField, String secondField, String type) {
        this.firstField = firstField;
        this.secondField = secondField;
        this.type = type;
    }

    public String getFirstField() { return firstField; }

    public String getSecondField() { return secondField; }

    public String getType() { return type; }

    @Override
    public String toString() {
        return "SearchDTO{" +
        "firstField='" + firstField + '\'' +
        ", secondField='" + secondField + '\'' +
        ", type='" + type + '\'' +
        '}';
    }
}
