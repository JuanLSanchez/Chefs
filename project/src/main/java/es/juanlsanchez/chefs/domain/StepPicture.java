package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A StepPicture.
 */
@Entity
@Table(name = "STEP_PICTURE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StepPicture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "src")
    private byte[] src;

    @Column(name = "properties")
    private String properties;

    /*Si se quiere quitar el JsonIgnore, se debe de poner @JsonManagedReference*/
    @ManyToOne(optional = false)
    @JsonIgnore
    private Step step;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getSrc() {
        return src;
    }

    public void setSrc(byte[] src) {
        this.src = src;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
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

        StepPicture stepPicture = (StepPicture) o;

        return Objects.equals(id, stepPicture.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "StepPicture{" +
                "id=" + id +
                ", title='" + title + "'" +
                ", src='" + src + "'" +
                ", properties='" + properties + "'" +
                '}';
    }

    public StepPicture copy() {
        StepPicture result;
        String[] ignore;

        //Ignore de relationShip and id
        ignore = new String[]{"id", "step"};
        result = new StepPicture();

        //Copy the object
        BeanUtils.copyProperties(this, result, ignore);

        return result;
    }
}
