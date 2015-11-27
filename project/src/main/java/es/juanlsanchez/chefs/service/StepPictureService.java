package es.juanlsanchez.chefs.service;

import es.juanlsanchez.chefs.domain.StepPicture;
import es.juanlsanchez.chefs.repository.StepPictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by juanlu on 27/11/15.
 */
@Service
@Transactional
public class StepPictureService {

    @Autowired
    private StepPictureRepository stepPictureRepository;

    public StepPicture save(StepPicture stepPicture) {
        StepPicture result;

        result = stepPictureRepository.save(stepPicture);

        return result;
    }
}
