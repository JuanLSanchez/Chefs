package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

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

    @OneToOne
    private SocialPicture socialPicture;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "SOCIAL_ENTITY_TAG",
               joinColumns = @JoinColumn(name="social_entitys_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="tags_id", referencedColumnName="ID"))
    private Set<Tag> tags = new HashSet<>();

    @OneToOne(mappedBy = "socialEntity")
    @JsonIgnore
    private Competition competition;

    @OneToMany(mappedBy = "socialEntity")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Assessment> assessments = new HashSet<>();

    @OneToMany(mappedBy = "socialEntity")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Comment> comments = new HashSet<>();

    @ManyToMany
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SocialEntity socialEntity = (SocialEntity) o;

        if ( ! Objects.equals(id, socialEntity.id)) return false;

        return true;
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
}
