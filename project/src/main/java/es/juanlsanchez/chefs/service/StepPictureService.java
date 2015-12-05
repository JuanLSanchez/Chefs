package es.juanlsanchez.chefs.service;

import com.google.common.collect.Sets;
import es.juanlsanchez.chefs.domain.StepPicture;
import es.juanlsanchez.chefs.repository.StepPictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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

    public Set<StepPicture> save(Set<StepPicture> stepPictures) {
        Set<StepPicture> result;

        result = Sets.newHashSet(stepPictureRepository.save(stepPictures));

        return result;
    }

    public void delete(StepPicture stepPicture) {
        stepPictureRepository.delete(stepPicture);
    }

    public void delete(Set<StepPicture> oldStepPicturesToRemove) {
        stepPictureRepository.deleteInBatch(oldStepPicturesToRemove);
    }
}
