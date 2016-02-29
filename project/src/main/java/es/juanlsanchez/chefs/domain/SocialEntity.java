package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A SocialEntity.
 */
@Entity
@Table(name = "SOCIAL_ENTITY")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class SocialEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "sum_rating")
    private Integer sumRating;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "public_inscription")
    private Boolean publicInscription;

    @Column(name = "blocked")
    private Boolean blocked;

    @OneToOne(mappedBy = "socialEntity")
    @JsonIgnore
    private Event event;

    @OneToOne(mappedBy = "socialEntity")
    @JsonIgnore
    private Recipe recipe;

    @OneToOne(cascade = CascadeType.ALL)
    private SocialPicture socialPicture;

    @ManyToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "SOCIAL_ENTITY_TAG",
               joinColumns = @JoinColumn(name="social_entitys_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="tags_id", referencedColumnName="ID"))
    private Set<Tag> tags = new HashSet<>();

    @OneToOne(mappedBy = "socialEntity")
    @JsonIgnore
    private Competition competition;

    @OneToMany(mappedBy = "socialEntity", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Assessment> assessments = new HashSet<>();

    @OneToMany(mappedBy = "socialEntity", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany
    @JsonSerialize
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "SOCIAL_ENTITY_USER",
               joinColumns = @JoinColumn(name="social_entitys_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="users_id", referencedColumnName="ID"))
    private Set<User> users = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSumRating() {
        return sumRating;
    }

    public void setSumRating(Integer sumRating) {
        this.sumRating = sumRating;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Boolean getPublicInscription() {
        return publicInscription;
    }

    public void setPublicInscription(Boolean publicInscription) {
        this.publicInscription = publicInscription;
    }

    public Boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(Boolean blocked) {
        this.blocked = blocked;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public SocialPicture getSocialPicture() {
        return socialPicture;
    }

    public void setSocialPicture(SocialPicture socialPicture) {
        this.socialPicture = socialPicture;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Set<Assessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(Set<Assessment> assessments) {
        this.assessments = assessments;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
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

        SocialEntity socialEntity = (SocialEntity) o;

        return Objects.equals(id, socialEntity.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "SocialEntity{" +
                "id=" + id +
                ", sumRating='" + sumRating + "'" +
                ", isPublic='" + isPublic + "'" +
                ", publicInscription='" + publicInscription + "'" +
                ", blocked='" + blocked + "'" +
                '}';
    }

    public SocialEntity copy() {
        SocialEntity result;
        String[] ignore;

        //Ignore de relationShip and id
        ignore = new String[]{"id", "recipe", "event", "users", "comments", "assessments",
            "competition", "socialPicture"};
        result = new SocialEntity();

        //Copy the object
        BeanUtils.copyProperties(this, result, ignore);

        //Set the socialPicture
        result.setSocialPicture(this.socialPicture.copy());

        return result;
    }
}
