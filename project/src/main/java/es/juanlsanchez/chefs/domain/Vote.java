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
 * A Vote.
 */
@Entity
@Table(name = "VOTE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Vote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "is_final")
    private Boolean isFinal;

    @Column(name = "abstain")
    private Boolean abstain;

    @Column(name = "comment")
    private String comment;

    @Column(name = "completed_score")
    private Boolean completedScore;

    @ManyToOne
    private Recipe recipe;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "vote")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Score> scores = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public Boolean getAbstain() {
        return abstain;
    }

    public void setAbstain(Boolean abstain) {
        this.abstain = abstain;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getCompletedScore() {
        return completedScore;
    }

    public void setCompletedScore(Boolean completedScore) {
        this.completedScore = completedScore;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
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

        Vote vote = (Vote) o;

        if ( ! Objects.equals(id, vote.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", isFinal='" + isFinal + "'" +
                ", abstain='" + abstain + "'" +
                ", comment='" + comment + "'" +
                ", completedScore='" + completedScore + "'" +
                '}';
    }
}
