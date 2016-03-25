package es.juanlsanchez.chefs.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import es.juanlsanchez.chefs.domain.Menu;
import es.juanlsanchez.chefs.domain.Schedule;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeDeserializer;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
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

    private Map<Long, List<String>> recipes = Maps.newHashMap();


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

    public Map<Long, List<String>> getRecipes() {
        return recipes;
    }

    public void setRecipes(Map<Long, List<String>> recipes) {
        this.recipes = recipes;
    }

    public MenuDTO(Menu menu) {
        this.id = menu.getId();
        this.time = menu.getTime();
        this.schedule = menu.getSchedule();
        this.recipes = menu.getRecipes().stream()
            .collect(Collectors.toMap(recipe -> recipe.getId(), recipe1 -> {
                List<String> list = Lists.newArrayList();
                list.add(recipe1.getName());
                list.add(recipe1.getUser().getLogin());
                return list;
            }));
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
