package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.ProfilePicture;
import es.juanlsanchez.chefs.repository.ProfilePictureRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import sun.java2d.cmm.Profile;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by juanlu on 30/01/16.
 */
@Service
@Transactional
public class ProfilePictureService {

    @Inject
    private ProfilePictureRepository profilePictureRepository;

    public ProfilePicture save(ProfilePicture profilePicture){
        ProfilePicture result;

        if(profilePicture.getId()!=null){
            result = Optional.ofNullable(profilePictureRepository.findOne(profilePicture.getId()))
                .filter(p -> p.getUser().getLogin().equals(SecurityUtils.getCurrentLogin()))
                .map(p -> profilePictureRepository.save(p))
                .orElse(null);
        }else{
            result = profilePictureRepository.save(profilePicture);
        }

        return result;
    }
}
