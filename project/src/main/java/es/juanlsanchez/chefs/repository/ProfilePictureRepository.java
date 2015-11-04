package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.ProfilePicture;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ProfilePicture entity.
 */
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture,Long> {

}
