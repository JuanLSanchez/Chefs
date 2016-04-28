package es.juanlsanchez.chefs.service;

import com.google.common.collect.Lists;
import es.juanlsanchez.chefs.domain.*;
import es.juanlsanchez.chefs.domain.enumeration.ActivityLogTypeEnum;
import es.juanlsanchez.chefs.domain.enumeration.ActivityLogVerbEnum;
import es.juanlsanchez.chefs.repository.ActivityLogRepository;
import es.juanlsanchez.chefs.web.rest.dto.ActivityLogDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * chefs
 * Created by juanlu on 11-abr-2016.
 */
@Service
@Transactional
public class ActivityLogService {

    private static final String SEPARATOR = ",";
    private static final String GELD = "/";
    @Autowired
    private ActivityLogRepository activityLogRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestService requestService;

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

    public Page<ActivityLogDTO> getPrincipalActivityLog(Pageable pageable) {
        Page<ActivityLogDTO> result;
        List<Request> requests;
        List<String> logins;

        requests = requestService.findRequestWithPrincipalAsFollower();

        logins = requests.stream()
            .filter(r -> r.getAccepted())
            .map(r -> r.getFollowed().getLogin())
            .collect(Collectors.toList());

        result = activityLogRepository.getActivityLogByLogin(logins, pageable);

        return result;
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
        tags = tagsToString(recipe.getSocialEntity());
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
            activityLogTypeEnum, tagsToString(comment.getSocialEntity()), verb, objcetId);
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
            activityLogTypeEnum, tagsToString(socialEntity), verb, objcetId);
    }

    /* Assessment activity log */
    public void createAssessment(Assessment assessment){
        saveAssessment(assessment, ActivityLogVerbEnum.CREATE);

    }

    public void updateAssessment(Assessment assessment) {
        saveAssessment(assessment, ActivityLogVerbEnum.UPDATE);
    }

    public void deleteAssessment(Assessment assessment){
        saveAssessment(assessment, ActivityLogVerbEnum.DELETE);
    }

    private void saveAssessment(Assessment assessment, ActivityLogVerbEnum verb){
        String name = "";
        Long objcetId = -1L;
        ActivityLogTypeEnum activityLogTypeEnum = ActivityLogTypeEnum.COMMENT;

        if(assessment.getSocialEntity().getRecipe()!=null){
            name = assessment.getSocialEntity().getRecipe().getName();
            objcetId = assessment.getSocialEntity().getRecipe().getId();
            activityLogTypeEnum = ActivityLogTypeEnum.ASSESSMENT_RECIPE;
        }else if(assessment.getSocialEntity().getCompetition()!=null){
            name = assessment.getSocialEntity().getCompetition().getName();
            objcetId = assessment.getSocialEntity().getCompetition().getId();
            activityLogTypeEnum = ActivityLogTypeEnum.ASSESSMENT_COMPETITION;
        }else if(assessment.getSocialEntity().getEvent()!=null){
            name = assessment.getSocialEntity().getEvent().getName();
            objcetId = assessment.getSocialEntity().getEvent().getId();
            activityLogTypeEnum = ActivityLogTypeEnum.ASSESSMENT_EVENT;
        }

        save(assessment.getRating().toString(), assessment.getUser().getLogin(), new DateTime(), name,
            assessment.getUser().getFirstName(), activityLogTypeEnum, tagsToString(assessment.getSocialEntity()),
            verb, objcetId);
    }


    /* Utilities */
    private String tagsToString(SocialEntity socialEntity) {
        return socialEntity.getTags().stream().limit(10).map(t -> tagToString(t))
            .collect(Collectors.joining(SEPARATOR + SEPARATOR));
    }

    public static List<Tag> stringToTags(String tags){
        if(tags != null && !tags.isEmpty()){
            return Arrays.asList(tags.split(SEPARATOR + SEPARATOR)).stream()
                .map(s -> stringToTag(reverseWithoutComme(s)))
                .collect(Collectors.toList());
        }else {
            return Lists.newArrayList();
        }
    }

    private static Tag stringToTag(String s) {
        Tag result;
        String[] array;

        array = s.split( SEPARATOR + SEPARATOR);

        result = new Tag();
        result.setId(new Long(array[0]));
        result.setName(reverseWithoutComme(array[1]));

        return result;
    }

    private String tagToString(Tag t) {
        return withoutComme(t.getId() + SEPARATOR + SEPARATOR + withoutComme(t.getName()));
    }

    private String withoutComme(String name) {
        return name.replaceAll(SEPARATOR, GELD + SEPARATOR);
    }

    private static String reverseWithoutComme(String name) {
        return name.replaceAll(GELD + SEPARATOR, SEPARATOR);
    }

}
