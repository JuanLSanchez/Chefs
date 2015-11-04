package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Food;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Food entity.
 */
public interface FoodRepository extends JpaRepository<Food,Long> {

}
