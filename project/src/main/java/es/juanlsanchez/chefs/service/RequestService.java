package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.Request;
import es.juanlsanchez.chefs.domain.User;
import es.juanlsanchez.chefs.repository.RequestRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import es.juanlsanchez.chefs.web.rest.dto.RequestInfoDTO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by juanlu on 11/02/16.
 */
@Service
@Transactional
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;


    public Request findRequestWithPrincipalAsFollowerAndFollowed(String followed) {
        return requestRepository.findRequestWithPrincipalAsFollowerAndFollowed(followed);
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

    private Request updateByFollower(Request request) {
        Request result;

        if(!request.getLocked()){
            if(request.getIgnored()){
                request.setIgnored(false);
                result = requestRepository.save(request);
            }else{
                requestRepository.delete(request);
                result = null;
            }
        }else{
            throw new IllegalArgumentException("The follower can not modify a locked request");
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
        Long nFollowers, nFollowed, nWaiting;

        nFollowers = requestRepository.countByFollowedLoginAndAccepted(login, true);
        nFollowed = requestRepository.countByFollowerLoginAndAccepted(login, true);

        if(SecurityUtils.isAuthenticated() && SecurityUtils.getCurrentLogin().equals(login)){
            nWaiting = requestRepository.countByFollowedLoginAndAcceptedAndLockedAndIgnored(login, false, false, false);
        }else{
            nWaiting = -1L;
        }

        result = new RequestInfoDTO(nFollowers, nFollowed, nWaiting);

        return result;
    }
}
