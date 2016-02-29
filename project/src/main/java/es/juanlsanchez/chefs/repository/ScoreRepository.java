package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Score;
import org.springframework.data.jpa.repository.*;

/**
 * Spring Data JPA repository for the Score entity.
 */
public interface ScoreRepository extends JpaRepository<Score,Long> {

}
