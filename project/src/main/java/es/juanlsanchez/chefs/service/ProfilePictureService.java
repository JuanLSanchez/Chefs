package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.ProfilePicture;
import es.juanlsanchez.chefs.repository.ProfilePictureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by juanlu on 30/01/16.
 */
@Service
@Transactional
public class ProfilePictureService {

    @Inject
    private ProfilePictureRepository profilePictureRepository;

    public ProfilePicture save(ProfilePicture profilePicture){
        return profilePictureRepository.save(profilePicture);
    }
}
