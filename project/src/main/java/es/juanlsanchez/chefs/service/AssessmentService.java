package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.ActivityLog;
import es.juanlsanchez.chefs.domain.Assessment;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.AssessmentRepository;
import es.juanlsanchez.chefs.repository.SocialEntityRepository;
import es.juanlsanchez.chefs.service.util.ErrorMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * chefs
 * Created by juanlu on 11-abr-2016.
 */
@Service
@Transactional
public class AssessmentService {

    @Autowired
    private AssessmentRepository assessmentRepository;
    @Autowired
    private SocialEntityRepository socialEntityRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private SocialEntityService socialEntityService;
    @Autowired
    private ActivityLogService activityLogService;

    public Double save(Long socialEntityId, Integer rating) {
        Integer size, sumRating;
        SocialEntity socialEntity;
        Assessment assessment;
        User principal;
        Double result;

        socialEntity = Optional.ofNullable(socialEntityService.findOne(socialEntityId))
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));

        principal = userService.getPrincipal();
        size = socialEntity.getAssessments().size();
        sumRating = socialEntity.getSumRating();

        /* Check if the user has assessment */
        assessment = Optional
            .ofNullable(assessmentRepository.findOneByUserLoginAndSocialEntityId(principal.getLogin(), socialEntityId))
            .orElseGet(() -> {
                Assessment a = new Assessment();
                a.setSocialEntity(socialEntity);
                a.setUser(principal);
                return a;
            });

        if(assessment.getId()==null){
            size++;
            activityLogService.createAssessment(assessment);
        }else {
            sumRating -= assessment.getRating();
            activityLogService.updateAssessment(assessment);
        }

        sumRating += rating;

        /* Modified entities */
        assessment.setRating(rating);
        socialEntity.setSumRating(sumRating);

        /* Save entities */
        assessmentRepository.save(assessment);
        socialEntityRepository.save(socialEntity);

        result = sumRating.doubleValue()/size.doubleValue();

        return result;
    }

    public Double findRatingBySocialEntityId(Long socialEntityId) {
        SocialEntity socialEntity;
        Double sumRating, size, result;

        socialEntity = socialEntityService.findOne(socialEntityId);
        sumRating = socialEntity.getSumRating().doubleValue();
        size = new Double(socialEntity.getAssessments().size());

        result = size>0?(sumRating/size):-1;

        return result;
    }

    public Integer findRatingOfUserBySocialEntityId(Long socialEntityId) {
        Assessment assessment;
        User principal;

        principal = userService.getPrincipal();
        assessment = findOneByUserLoginAndSocialEntityId(principal.getLogin(), socialEntityId)
            .orElse(new Assessment());

        return assessment.getRating();
    }

    private Optional<Assessment> findOneByUserLoginAndSocialEntityId(String login, Long socialEntityId) {
        Optional<Assessment> result;

        result = Optional
            .ofNullable(assessmentRepository.findOneByUserLoginAndSocialEntityId(login, socialEntityId));


        return result;
    }

    public Double deleteBySocialEntityId(Long socialEntityId) {
        Assessment assessment;
        SocialEntity socialEntity;
        User principal;
        Double sumRating, size, result;

        /* Check principal */
        principal = userService.getPrincipal();
        assessment = findOneByUserLoginAndSocialEntityId(principal.getLogin(), socialEntityId)
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));

        /* Modified social entity */
        socialEntity = socialEntityService.findOne(socialEntityId);
        sumRating = socialEntity.getSumRating().doubleValue();
        size = new Double(socialEntity.getAssessments().size());
        socialEntity.setSumRating(socialEntity.getSumRating() - assessment.getRating());

        /* Create result */
        size --;
        sumRating -= assessment.getRating();
        result = sumRating/size;

        activityLogService.deleteAssessment(assessment);

        /* Delete assessment */
        assessmentRepository.delete(assessment);

        return result;
    }
}
