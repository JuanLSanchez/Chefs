package es.juanlsanchez.chefs.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Lists;
import es.juanlsanchez.chefs.domain.ActivityLog;
import es.juanlsanchez.chefs.domain.Tag;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeDeserializer;
import es.juanlsanchez.chefs.domain.util.CustomDateTimeSerializer;
import es.juanlsanchez.chefs.service.ActivityLogService;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Id;
import java.util.List;

/**
 * chefs
 * Created by juanlu on 28-abr-2016.
 */
public class ActivityLogDTO {

    @Id
    private Long id;

    private String login;

    private String nameOfCustomer;

    private String pictureUrl;

    private String objectType;

    private Long objectId;

    private String verb;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private DateTime moment;

    private String name;

    private String description;

    private List<Tag> tags;

    public ActivityLogDTO(ActivityLog activityLog){
        this(activityLog.getId(), activityLog.getLogin(), activityLog.getNameOfCustomer(),
            activityLog.getPictureUrl(), activityLog.getObjectType(), activityLog.getObjectId(), activityLog.getVerb(),
            activityLog.getMoment(), activityLog.getName(), activityLog.getDescription(), Lists.newArrayList());

        this.tags = ActivityLogService.stringToTags(activityLog.getTags());

    }

    private ActivityLogDTO(Long id, String login, String nameOfCustomer, String pictureUrl, String objectType,
                          Long objectId, String verb, DateTime moment, String name, String description,
                          List<Tag> tags) {
        this.id = id;
        this.login = login;
        this.nameOfCustomer = nameOfCustomer;
        this.pictureUrl = pictureUrl;
        this.objectType = objectType;
        this.objectId = objectId;
        this.verb = verb;
        this.moment = moment;
        this.name = name;
        this.description = description;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
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

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ActivityLogDTO{" +
            "id=" + id +
            ", login='" + login + '\'' +
            ", nameOfCustomer='" + nameOfCustomer + '\'' +
            ", pictureUrl='" + pictureUrl + '\'' +
            ", objectType='" + objectType + '\'' +
            ", objectId=" + objectId +
            ", verb='" + verb + '\'' +
            ", moment=" + moment +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", tags=" + tags +
            '}';
    }
}
