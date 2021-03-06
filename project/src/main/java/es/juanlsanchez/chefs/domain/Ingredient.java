package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import es.juanlsanchez.chefs.domain.enumeration.Measurement;
import org.springframework.beans.BeanUtils;

/**
 * A Ingredient.
 */
@Entity
@Table(name = "INGREDIENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Ingredient implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Min(value = 0)
    @Column(name = "amount")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement")
    private Measurement measurement;

    /*Si se quiere quitar el JsonIgnore, se debe de poner @JsonManagedReference*/
    @ManyToOne(optional = false)
    @JsonIgnore
    private Step step;

    @ManyToOne(optional = false)
    private Food food;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
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

        Ingredient ingredient = (Ingredient) o;

        return Objects.equals(id, ingredient.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", amount='" + amount + "'" +
                ", measurement='" + measurement + "'" +
                '}';
    }

    public Ingredient copy() {
        Ingredient result;
        String[] ignore;

        //Ignore de relationShip and id
        ignore = new String[]{"id", "step"};
        result = new Ingredient();

        //Copy the object
        BeanUtils.copyProperties(this, result, ignore);

        return result;
    }
}
