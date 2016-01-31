package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.BackgroundPicture;
import es.juanlsanchez.chefs.repository.BackgroundPictureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by juanlu on 30/01/16.
 */
@Service
@Transactional
public class BackgroundPictureService {

    @Inject
    private BackgroundPictureRepository backgroundPictureRepository;

    public BackgroundPicture save(BackgroundPicture backgroundPicture){
        return backgroundPictureRepository.save(backgroundPicture);
    }
}
