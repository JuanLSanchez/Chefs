package es.juanlsanchez.chefs.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.juanlsanchez.chefs.domain.Recipe;
import es.juanlsanchez.chefs.domain.Tag;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeDeserializer;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A Recipe.
 */
public class RecipeMiniDTO implements Serializable {

    @Id
    private Long id;

    private String name;

    private String description;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime updateDate;

    private Integer nSteps;

    private Integer nLike;

    private Integer nComments;

    private Double avgAssessment;

    private Set<Tag> tags;

    private String user;

    private Long fatherId;

    private Long socialEntityId;

    private byte[] picture;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public DateTime getUpdateDate() {
        return updateDate;
    }

    public Integer getnSteps() {
        return nSteps;
    }

    public Integer getnLike() {
        return nLike;
    }

    public Integer getnComments() {
        return nComments;
    }

    public Double getAvgAssessment() {
        return avgAssessment;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public String getUser() {
        return user;
    }

    public Long getFatherId() {
        return fatherId;
    }

    public Long getSocialEntityId() {
        return socialEntityId;
    }

    public byte[] getPicture() {
        return picture;
    }

    public RecipeMiniDTO(Long id, String name, String description, DateTime updateDate, int nSteps,
                         int nLike, int nComments, double avgAssessment, Set<Tag> tags,
                         String userLogin, Long fatherId, byte[] picture, Long socialEntityId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.updateDate = updateDate;
        this.nSteps = nSteps;
        this.nLike = nLike;
        this.nComments = nComments;
        this.avgAssessment = avgAssessment;
        this.tags = tags.stream().limit(10).collect(Collectors.toSet());
        this.user = userLogin;
        this.fatherId = fatherId;
        this.picture = picture;
        this.socialEntityId = socialEntityId;
    }

    public RecipeMiniDTO(Recipe recipe){
        this(recipe.getId(), recipe.getName(), recipe.getDescription(), recipe.getUpdateDate(),
            recipe.getSteps().size(), recipe.getSocialEntity().getUsers().size(),
            recipe.getSocialEntity().getComments().size(),
            recipe.getSocialEntity().getAssessments().size()>0?
                recipe.getSocialEntity().getSumRating().doubleValue()/recipe.getSocialEntity().getAssessments().size()
                :-1,
            recipe.getSocialEntity().getTags(), recipe.getUser().getLogin(),
            recipe.getFather()==null?null:recipe.getFather().getId(),
            recipe.getSocialEntity().getSocialPicture().getSrc(),
            recipe.getSocialEntity().getId());
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

        RecipeMiniDTO recipe = (RecipeMiniDTO) o;

        return Objects.equals(id, recipe.id);

    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", updateDate='" + updateDate + "'" +
                '}';
    }


}
