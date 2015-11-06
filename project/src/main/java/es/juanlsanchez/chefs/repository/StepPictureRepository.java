package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.StepPicture;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the StepPicture entity.
 */
public interface StepPictureRepository extends JpaRepository<StepPicture,Long> {

}
