package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.BackgroundPicture;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the BackgroundPicture entity.
 */
public interface BackgroundPictureRepository extends JpaRepository<BackgroundPicture,Long> {

}
