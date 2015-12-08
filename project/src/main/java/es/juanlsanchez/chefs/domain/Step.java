package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.BeanUtils;

import javax.inject.Inject;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Step.
 */
@Entity
@Table(name = "STEP")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Step implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Min(value = 0)
    @Column(name = "position")
    private Integer position;

    @NotNull
    @Column(name = "section", nullable = false)
    private String section;

    @ManyToOne(optional = false)
    @JsonIgnore
    private Recipe recipe;

    @OneToMany(mappedBy = "step", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<StepPicture> stepPicture = new HashSet<>();

    @OneToMany(mappedBy = "step", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Ingredient> ingredients = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Set<StepPicture> getStepPicture() {
        return stepPicture;
    }

    public void setStepPicture(Set<StepPicture> stepPicture) {
        this.stepPicture = stepPicture;
    }

    public Set<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (id == null){return false;}
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Step step = (Step) o;

        if ( ! Objects.equals(id, step.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Step{" +
                "id=" + id +
                ", position='" + position + "'" +
                ", section='" + section + "'" +
                '}';
    }

    public Step copy() {
        Step result;
        String[] ignore;
        Set<StepPicture> stepPictures;
        Set<Ingredient> ingredients;

        //Ignore de relationShip and id
        ignore = new String[]{"id", "recipe", "ingredients", "stepPicture"};
        result = new Step();

        stepPictures = Sets.newHashSet();
        ingredients = Sets.newHashSet();

        //Copy the object
        BeanUtils.copyProperties(this, result, ignore);

        //Set the stepPictures
        stepPictures.addAll(getStepPicture().stream().map(StepPicture::copy).collect(Collectors.toList()));
        result.setStepPicture(stepPictures);

        //Set the ingredients
        ingredients.addAll(getIngredients().stream().map(Ingredient::copy).collect(Collectors.toList()));
        result.setIngredients(ingredients);

        return result;
    }
}
