package es.juanlsanchez.chefs.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.juanlsanchez.chefs.domain.Comment;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeDeserializer;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Objects;

/**
 * A Comment.
 */
public class CommentDTO implements Serializable {
    private Long id;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime creationMoment;

    private String body;

    private UserDTO user;

    private Long socialEntityId;

    public CommentDTO(Comment comment){
        this.id = comment.getId();
        this.creationMoment = comment.getCreationMoment();
        this.body = comment.getBody();
        this.user = new UserDTO(comment.getUser());
        this.socialEntityId = comment.getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getCreationMoment() {
        return creationMoment;
    }

    public void setCreationMoment(DateTime creationMoment) {
        this.creationMoment = creationMoment;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Long getSocialEntity() {
        return socialEntityId;
    }

    public void setSocialEntity(Long socialEntityId) {
        this.socialEntityId = socialEntityId;
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

        CommentDTO comment = (CommentDTO) o;

        return Objects.equals(id, comment.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", creationMoment='" + creationMoment + "'" +
                ", body='" + body + "'" +
                '}';
    }
}
