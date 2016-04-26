package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.*;
import es.juanlsanchez.chefs.domain.enumeration.ActivityLogTypeEnum;
import es.juanlsanchez.chefs.domain.enumeration.ActivityLogVerbEnum;
import es.juanlsanchez.chefs.repository.ActivityLogRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * chefs
 * Created by juanlu on 11-abr-2016.
 */
@Service
@Transactional
public class ActivityLogService {

    private static final String SEPARATOR = ",";
    private static final String GELD = "\\";
    @Autowired
    private ActivityLogRepository activityLogRepository;

    /* CRUD */
    private void save(String description, String login, DateTime moment, String name, String nameOfCustomer,
                      ActivityLogTypeEnum type, String tags, ActivityLogVerbEnum verb, Long objcetId) {
        ActivityLog activityLog;

        activityLog = new ActivityLog();
        activityLog.setDescription(description);
        activityLog.setLogin(login);
        activityLog.setMoment(moment);
        activityLog.setName(name);
        activityLog.setNameOfCustomer(nameOfCustomer);
        activityLog.setObjectType(type.toString());
        activityLog.setTags(tags);
        activityLog.setVerb(verb.toString());
        activityLog.setObjectId(objcetId);

        activityLogRepository.save(activityLog);
    }

    /* Recipe activity log*/
    public void createRecipe(Recipe recipe) {
        saveRecipe(recipe, ActivityLogVerbEnum.CREATE);
    }
    public void deleteRecipe(Recipe recipe) {
        saveRecipe(recipe, ActivityLogVerbEnum.DELETE);
        activityLogRepository.dropObjectId(recipe.getId(), ActivityLogTypeEnum.RECIPE.toString());
    }
    public void updateRecipe(Recipe recipe) {
        saveRecipe(recipe, ActivityLogVerbEnum.UPDATE);
    }

    private void saveRecipe(Recipe recipe, ActivityLogVerbEnum verb) {
        String description, name, nameOfCustomer, tags, login;
        Long objcetId;

        login = recipe.getUser().getLogin();
        nameOfCustomer = recipe.getUser().getFirstName();

        description = recipe.getDescription();
        name = recipe.getName();
        tags = tagToString(recipe.getSocialEntity());
        objcetId = recipe.getId();

        save(description, login, new DateTime(), name, nameOfCustomer, ActivityLogTypeEnum.RECIPE,
            tags, verb, objcetId);
    }


    /* Comment activity log */
    public void createComment(Comment comment){
        saveComment(comment, ActivityLogVerbEnum.CREATE);
    }
    public void updateComment(Comment comment){
        saveComment(comment, ActivityLogVerbEnum.UPDATE);
    }
    public void deleteComment(Comment comment){
        saveComment(comment, ActivityLogVerbEnum.DELETE);
    }

    private void saveComment(Comment comment, ActivityLogVerbEnum verb){
        String name = "";
        Long objcetId = -1L;
        ActivityLogTypeEnum activityLogTypeEnum = ActivityLogTypeEnum.COMMENT;

        if(comment.getSocialEntity().getRecipe()!=null){
            name = comment.getSocialEntity().getRecipe().getName();
            objcetId = comment.getSocialEntity().getRecipe().getId();
            activityLogTypeEnum = ActivityLogTypeEnum.COMMENT_RECIPE;
        }else if(comment.getSocialEntity().getCompetition()!=null){
            name = comment.getSocialEntity().getCompetition().getName();
            objcetId = comment.getSocialEntity().getCompetition().getId();
            activityLogTypeEnum = ActivityLogTypeEnum.COMMENT_COMPETITION;
        }else if(comment.getSocialEntity().getEvent()!=null){
            name = comment.getSocialEntity().getEvent().getName();
            objcetId = comment.getSocialEntity().getEvent().getId();
            activityLogTypeEnum = ActivityLogTypeEnum.COMMENT_EVENT;
        }

        save(comment.getBody(), comment.getUser().getLogin(), new DateTime(), name, comment.getUser().getFirstName(),
            activityLogTypeEnum, tagToString(comment.getSocialEntity()), verb, objcetId);
    }

    /* Like activity log*/
    public void createLike(SocialEntity socialEntity, User user){
        saveLike(socialEntity, ActivityLogVerbEnum.CREATE, user);
    }
    public void deleteLike(SocialEntity socialEntity, User user){
        saveLike(socialEntity, ActivityLogVerbEnum.DELETE, user);
    }

    private void saveLike(SocialEntity socialEntity, ActivityLogVerbEnum verb, User user){
        String name = "", description = "";
        Long objcetId = -1L;
        ActivityLogTypeEnum activityLogTypeEnum = ActivityLogTypeEnum.COMMENT;

        if(socialEntity.getRecipe()!=null){
            name = socialEntity.getRecipe().getName();
            objcetId = socialEntity.getRecipe().getId();
            description = socialEntity.getRecipe().getDescription();
            activityLogTypeEnum = ActivityLogTypeEnum.LIKE_RECIPE;
        }else if(socialEntity.getCompetition()!=null){
            name = socialEntity.getCompetition().getName();
            objcetId = socialEntity.getCompetition().getId();
            description = socialEntity.getCompetition().getDescription();
            activityLogTypeEnum = ActivityLogTypeEnum.LIKE_COMPETITION;
        }else if(socialEntity.getEvent()!=null){
            name = socialEntity.getEvent().getName();
            objcetId = socialEntity.getEvent().getId();
            description = socialEntity.getEvent().getDescription();
            activityLogTypeEnum = ActivityLogTypeEnum.LIKE_EVENT;
        }

        save(description, user.getLogin(), new DateTime(), name, user.getFirstName(),
            activityLogTypeEnum, tagToString(socialEntity), verb, objcetId);
    }


    /* Utilities */
    private String tagToString(SocialEntity socialEntity) {
        return socialEntity.getTags().stream().map(t -> withoutComme(t.getName()))
            .collect(Collectors.joining(SEPARATOR + SEPARATOR));
    }
    private String withoutComme(String name) {
        return name.replaceAll(SEPARATOR, GELD + SEPARATOR);
    }

}
