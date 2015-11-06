package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Assessment;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Assessment entity.
 */
public interface AssessmentRepository extends JpaRepository<Assessment,Long> {

    @Query("select assessment from Assessment assessment where assessment.user.login = ?#{principal.username}")
    List<Assessment> findByUserIsCurrentUser();

}
