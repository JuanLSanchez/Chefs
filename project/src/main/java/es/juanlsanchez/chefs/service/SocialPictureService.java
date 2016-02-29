package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.SocialPicture;
import es.juanlsanchez.chefs.repository.SocialPictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by juanlu on 29/02/16.
 */
@Service
@Transactional
public class SocialPictureService {

    @Autowired
    private SocialPictureRepository socialPictureRepository;


    public SocialPicture save(SocialPicture socialPicture) {
        return socialPictureRepository.save(socialPicture);
    }
}
