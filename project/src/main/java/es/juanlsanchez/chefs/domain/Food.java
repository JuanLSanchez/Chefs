package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Food.
 */
@Entity
@Table(name = "FOOD")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Food implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull        
    @Column(name = "normalizaed_name", nullable = false)
    private String normalizaedName;

    @NotNull        
    @Column(name = "name", nullable = false)
    private String name;

    @Min(value = 0)        
    @Column(name = "kcal")
    private Double kcal;

    @OneToMany(mappedBy = "food")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Ingredient> ingredientss = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNormalizaedName() {
        return normalizaedName;
    }

    public void setNormalizaedName(String normalizaedName) {
        this.normalizaedName = normalizaedName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getKcal() {
        return kcal;
    }

    public void setKcal(Double kcal) {
        this.kcal = kcal;
    }

    public Set<Ingredient> getIngredientss() {
        return ingredientss;
    }

    public void setIngredientss(Set<Ingredient> ingredients) {
        this.ingredientss = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Food food = (Food) o;

        if ( ! Objects.equals(id, food.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Food{" +
                "id=" + id +
                ", normalizaedName='" + normalizaedName + "'" +
                ", name='" + name + "'" +
                ", kcal='" + kcal + "'" +
                '}';
    }
}
