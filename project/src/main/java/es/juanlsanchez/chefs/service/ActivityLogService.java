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
    public void create(Recipe recipe) {
        saveRecipe(recipe, ActivityLogVerbEnum.CREATE);
    }
    public void delete(Recipe recipe) {
        saveRecipe(recipe, ActivityLogVerbEnum.DELETE);
        activityLogRepository.dropObjectId(recipe.getId(), ActivityLogTypeEnum.RECIPE.toString());
    }
    public void update(Recipe recipe) {
        saveRecipe(recipe, ActivityLogVerbEnum.UPDATE);
    }

    private void saveRecipe(Recipe recipe, ActivityLogVerbEnum verb) {
        String description, name, nameOfCustomer, tags, login;
        Long objcetId;

        login = recipe.getUser().getLogin();
        nameOfCustomer = recipe.getUser().getFirstName();

        description = recipe.getDescription();
        name = recipe.getName();
        tags = recipe.getSocialEntity().getTags().stream().map(t -> withoutComme(t.getName()))
            .collect(Collectors.joining(SEPARATOR + SEPARATOR));
        objcetId = recipe.getId();

        save(description, login, new DateTime(), name, nameOfCustomer, ActivityLogTypeEnum.RECIPE,
            tags, verb, objcetId);
    }


    /* Utilities */
    private String withoutComme(String name) {
        return name.replaceAll(SEPARATOR, GELD + SEPARATOR);
    }

}
