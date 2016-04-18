package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Assessment;
import es.juanlsanchez.chefs.domain.SocialEntity;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.AssessmentRepository;
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
    private UserService userService;
    @Autowired
    private SocialEntityService socialEntityService;

    public Double save(Long socialEntityId, Integer rating) {
        Integer size, sumRating;
        SocialEntity socialEntity;
        Assessment assessment;
        User principal;

        socialEntity = Optional.ofNullable(socialEntityService.findOne(socialEntityId))
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));
        principal = userService.getPrincipal();
        size = socialEntity.getAssessments().size();
        sumRating = socialEntity.getSumRating();

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
        }else {
            sumRating -= assessment.getRating();
        }

        sumRating += rating;

        assessment.setRating(rating);

        assessmentRepository.save(assessment);

        return sumRating.doubleValue()/size.doubleValue();
    }

    public Double findRatingBySocialEntityId(Long socialEntityId) {
        SocialEntity socialEntity;

        socialEntity = socialEntityService.findOne(socialEntityId);

        return socialEntity.getSumRating().doubleValue() / socialEntity.getAssessments().size();
    }

    public Integer findRatingOfUserBySocialEntityId(Long socialEntityId) {
        Assessment assessment;
        User principal;

        principal = userService.getPrincipal();
        assessment = findOneByUserLoginAndSocialEntityId(principal.getLogin(), socialEntityId)
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));

        return assessment.getRating();
    }

    private Optional<Assessment> findOneByUserLoginAndSocialEntityId(String login, Long socialEntityId) {
        Optional<Assessment> result;

        result = Optional
            .ofNullable(assessmentRepository.findOneByUserLoginAndSocialEntityId(login, socialEntityId));


        return result;
    }

    public void deleteBySocialEntityId(Long socialEntityId) {
        Assessment assessment;
        SocialEntity socialEntity;
        User principal;

        principal = userService.getPrincipal();
        assessment = findOneByUserLoginAndSocialEntityId(principal.getLogin(), socialEntityId)
            .orElseThrow(() -> new IllegalArgumentException(ErrorMessageService.ILLEGAL_SOCIAL_ENTITY));

        socialEntity = socialEntityService.findOne(socialEntityId);
        socialEntity.setSumRating(socialEntity.getSumRating() - assessment.getRating());

        assessmentRepository.delete(assessment);
    }
}
