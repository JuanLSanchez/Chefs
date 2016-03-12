package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.BackgroundPicture;
import es.juanlsanchez.chefs.domain.ProfilePicture;
import es.juanlsanchez.chefs.repository.BackgroundPictureRepository;
import es.juanlsanchez.chefs.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by juanlu on 30/01/16.
 */
@Service
@Transactional
public class BackgroundPictureService {

    @Inject
    private BackgroundPictureRepository backgroundPictureRepository;

    public BackgroundPicture save(BackgroundPicture backgroundPicture){
        BackgroundPicture result;

        if(backgroundPicture.getId()!=null){
            result = Optional.ofNullable(backgroundPictureRepository.findOne(backgroundPicture.getId()))
                .filter(b -> b.getUser().getLogin().equals(SecurityUtils.getCurrentLogin()))
                .map(b -> backgroundPictureRepository.save(b))
                .orElse(null);
        }else{
            result = backgroundPictureRepository.save(backgroundPicture);
        }
        return result;
    }
}
