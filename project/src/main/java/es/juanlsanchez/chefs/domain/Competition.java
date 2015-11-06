package es.juanlsanchez.chefs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.juanlsanchez.chefs.domain.util.CustomLocalDateSerializer;
import es.juanlsanchez.chefs.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Competition.
 */
@Entity
@Table(name = "COMPETITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Competition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull        
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull        
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @NotNull        
    @Column(name = "rules", nullable = false)
    private String rules;

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "inscription_time", nullable = false)
    private LocalDate inscriptionTime;

    @NotNull
    @Min(value = 0)        
    @Column(name = "max_nrecipes_by_chefs", nullable = false)
    private Integer maxNRecipesByChefs;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;
    
    @Column(name = "completed_score")
    private Boolean completedScore;
    
    @Column(name = "public_jury")
    private Boolean publicJury;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "COMPETITION_RECIPE",
               joinColumns = @JoinColumn(name="competitions_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="recipes_id", referencedColumnName="ID"))
    private Set<Recipe> recipes = new HashSet<>();

    @OneToMany(mappedBy = "competition")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Opinion> opinions = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "COMPETITION_USER",
               joinColumns = @JoinColumn(name="competitions_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="users_id", referencedColumnName="ID"))
    private Set<User> users = new HashSet<>();

    @ManyToOne
    private User owner;

    @OneToOne
    private SocialEntity socialEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public LocalDate getInscriptionTime() {
        return inscriptionTime;
    }

    public void setInscriptionTime(LocalDate inscriptionTime) {
        this.inscriptionTime = inscriptionTime;
    }

    public Integer getMaxNRecipesByChefs() {
        return maxNRecipesByChefs;
    }

    public void setMaxNRecipesByChefs(Integer maxNRecipesByChefs) {
        this.maxNRecipesByChefs = maxNRecipesByChefs;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getCompletedScore() {
        return completedScore;
    }

    public void setCompletedScore(Boolean completedScore) {
        this.completedScore = completedScore;
    }

    public Boolean getPublicJury() {
        return publicJury;
    }

    public void setPublicJury(Boolean publicJury) {
        this.publicJury = publicJury;
    }

    public Set<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }

    public Set<Opinion> getOpinions() {
        return opinions;
    }

    public void setOpinions(Set<Opinion> opinions) {
        this.opinions = opinions;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User user) {
        this.owner = user;
    }

    public SocialEntity getSocialEntity() {
        return socialEntity;
    }

    public void setSocialEntity(SocialEntity socialEntity) {
        this.socialEntity = socialEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Competition competition = (Competition) o;

        if ( ! Objects.equals(id, competition.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", deadline='" + deadline + "'" +
                ", rules='" + rules + "'" +
                ", inscriptionTime='" + inscriptionTime + "'" +
                ", maxNRecipesByChefs='" + maxNRecipesByChefs + "'" +
                ", creationDate='" + creationDate + "'" +
                ", completedScore='" + completedScore + "'" +
                ", publicJury='" + publicJury + "'" +
                '}';
    }
}
