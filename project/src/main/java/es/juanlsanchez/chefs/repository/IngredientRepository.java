package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Ingredient;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Ingredient entity.
 */
public interface IngredientRepository extends JpaRepository<Ingredient,Long> {

}
