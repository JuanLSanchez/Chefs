package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A SocialPicture.
 */
@Entity
@Table(name = "SOCIAL_PICTURE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SocialPicture implements Serializable {

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

    @OneToOne(mappedBy = "socialPicture")
    @JsonIgnore
    private SocialEntity socialEntity;

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

    public SocialEntity getSocialEntity() {
        return socialEntity;
    }

    public void setSocialEntity(SocialEntity socialEntity) {
        this.socialEntity = socialEntity;
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

        SocialPicture socialPicture = (SocialPicture) o;

        return Objects.equals(id, socialPicture.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SocialPicture{" +
                "id=" + id +
                ", title='" + title + "'" +
                ", src='" + src + "'" +
                ", properties='" + properties + "'" +
                '}';
    }

    public SocialPicture copy() {
        SocialPicture result;
        String[] ignore;

        //Ignore de relationShip and id
        ignore = new String[]{"id", "socialEntity"};
        result = new SocialPicture();

        //Copy the object
        BeanUtils.copyProperties(this, result, ignore);

        return result;
    }
}
