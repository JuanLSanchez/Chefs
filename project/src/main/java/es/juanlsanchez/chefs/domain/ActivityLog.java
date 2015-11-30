package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeDeserializer;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A ActivityLog.
 */
@Entity
@Table(name = "ACTIVITY_LOG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ActivityLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "id_of_customer")
    private Integer idOfCustomer;

    @Column(name = "name_of_customer")
    private String nameOfCustomer;

    @Column(name = "picture_url")
    private String pictureUrl;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "verb")
    private String verb;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "moment")
    private DateTime moment;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "tags")
    private String tags;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdOfCustomer() {
        return idOfCustomer;
    }

    public void setIdOfCustomer(Integer idOfCustomer) {
        this.idOfCustomer = idOfCustomer;
    }

    public String getNameOfCustomer() {
        return nameOfCustomer;
    }

    public void setNameOfCustomer(String nameOfCustomer) {
        this.nameOfCustomer = nameOfCustomer;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public DateTime getMoment() {
        return moment;
    }

    public void setMoment(DateTime moment) {
        this.moment = moment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

        ActivityLog activityLog = (ActivityLog) o;

        if ( ! Objects.equals(id, activityLog.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ActivityLog{" +
                "id=" + id +
                ", idOfCustomer='" + idOfCustomer + "'" +
                ", nameOfCustomer='" + nameOfCustomer + "'" +
                ", pictureUrl='" + pictureUrl + "'" +
                ", objectType='" + objectType + "'" +
                ", verb='" + verb + "'" +
                ", moment='" + moment + "'" +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", tags='" + tags + "'" +
                '}';
    }
}
