package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Opinion;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the Opinion entity.
 */
public interface OpinionRepository extends JpaRepository<Opinion,Long> {

}
