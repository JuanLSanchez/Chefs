package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.SocialPicture;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the SocialPicture entity.
 */
public interface SocialPictureRepository extends JpaRepository<SocialPicture,Long> {

}
