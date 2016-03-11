package es.juanlsanchez.chefs.service;

import com.google.common.collect.Lists;
import es.juanlsanchez.chefs.domain.Request;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.RequestRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.web.rest.dto.RequestInfoDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * chefs
 * Created by juanlu on 10-mar-2016.
 */
@Service
@Transactional
public class RequestService {

    public static final String ACCEPTED_STATUS = "accepted";
    public static final String LOCKED_STATUS = "locked";
    public static final String IGNORED_STATUS = "ignored";
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RecipeService recipeService;


    public Request findRequestWithPrincipalAsFollowerAndFollowed(String followed) {
        return requestRepository.findRequestWithPrincipalAsFollowerAndFollowed(followed);
    }

    public Request findRequestWithPrincipalAsFollowedAndFollower(String follower) {
        return requestRepository.findRequestWithPrincipalAsFollowedAndFollower(follower);
    }

    public Request update(String followed) {
        Request result;

        result = requestRepository.findRequestWithPrincipalAsFollowerAndFollowed(followed);

        if (result == null){
            result = saveByFollower(followed);
        }else{
            result = updateByFollower(result);
        }

        return result;
    }

    private Request updateByFollower (Request request) {
        Request result;

        Assert.isTrue(!request.getLocked(), "The follower can not modify a locked request");

        if(request.getIgnored()){
            request.setIgnored(false);
            result = requestRepository.save(request);
        }else{
            requestRepository.delete(request);
            result = null;
        }

        return result;
    }

    private Request saveByFollower(String followed) {
        Request result;
        User userFollowed, userFollower;

        userFollowed = userService.getUserWithLogin(followed).get();
        userFollower = userService.getPrincipal();

        if( userFollowed==null){
            throw new IllegalArgumentException("The follower does not exist");
        }else if (userFollower == null) {
            throw new IllegalArgumentException("The follower does not exist");
        }

        result = new Request();

        result.setAccepted(false);
        result.setIgnored(false);
        result.setLocked(false);

        result.setCreationDate(new DateTime());

        result.setFollowed(userFollowed);
        result.setFollower(userFollower);

        result = requestRepository.save(result);

        return result;
    }

    public RequestInfoDTO getCountFollowersAndFollowed(String login) {
        RequestInfoDTO result;
        Long nFollowers, nFollowed, nWaiting, nRecipes;
        boolean isPrincipal;

        nFollowers = requestRepository.countByFollowedLoginAndAccepted(login, true);
        nFollowed = requestRepository.countByFollowerLoginAndAccepted(login, true);
        nRecipes = recipeService.countByUserLoginAndSocialEntityBlocked(login, false);

        try {
            isPrincipal = SecurityUtils.getCurrentLogin().equals(login);
        }catch(Exception e){
            isPrincipal = false;
        }

        if(isPrincipal){
            nWaiting = requestRepository.countByFollowedLoginAndAcceptedAndLockedAndIgnored(login, false, false, false);
        }else{
            nWaiting = -1L;
        }

        result = new RequestInfoDTO(nFollowers, nFollowed, nWaiting, nRecipes);

        return result;
    }

    public Request update(String follower, String status) {
        Request result;
        List<String> states;
        int index;

        result = requestRepository.findRequestWithPrincipalAsFollowedAndFollower(follower);

        states = Lists.newArrayList(ACCEPTED_STATUS, LOCKED_STATUS, IGNORED_STATUS);
        index = states.indexOf(status);

        if (index >= 0){
            result.setAccepted(false);
            result.setLocked(false);
            result.setIgnored(false);
        }
        if (index == 0){
            result.setAccepted(true);
        }else if(index == 1){
            result.setLocked(true);
        }else if(index == 2){
            result.setIgnored(true);
        }

        if (index >= 0){
            result = requestRepository.save(result);
        }

        return result;
    }
}
