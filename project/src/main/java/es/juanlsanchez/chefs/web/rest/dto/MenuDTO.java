package es.juanlsanchez.chefs.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.juanlsanchez.chefs.domain.Menu;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Schedule;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeDeserializer;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * chefs
 * Created by juanlu on 23-mar-2016.
 */
public class MenuDTO {

    @Id
    private Long id;

    @NotNull
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime time;

    private Schedule schedule;

    private Set<Long> recipes = new HashSet<>();


    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Set<Long> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Long> recipes) {
        this.recipes = recipes;
    }

    public MenuDTO(Menu menu) {
        this.id = menu.getId();
        this.time = menu.getTime();
        this.schedule = menu.getSchedule();
        this.recipes = menu.getRecipes().stream()
            .map(Recipe::getId)
            .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "MenuDTO{" +
            "id=" + id +
            ", time=" + time +
            ", schedule=" + schedule +
            ", recipes=" + recipes +
            '}';
    }
}
