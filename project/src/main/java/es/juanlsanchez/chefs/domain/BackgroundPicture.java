package es.juanlsanchez.chefs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A BackgroundPicture.
 */
@Entity
@Table(name = "BACKGROUND_PICTURE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BackgroundPicture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "url")
    private String url;
    
    @Column(name = "properties")
    private String properties;

    @OneToOne
    private User user;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BackgroundPicture backgroundPicture = (BackgroundPicture) o;

        if ( ! Objects.equals(id, backgroundPicture.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BackgroundPicture{" +
                "id=" + id +
                ", title='" + title + "'" +
                ", url='" + url + "'" +
                ", properties='" + properties + "'" +
                '}';
    }
}
