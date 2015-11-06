package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Step;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Step entity.
 */
public interface StepRepository extends JpaRepository<Step,Long> {

}
