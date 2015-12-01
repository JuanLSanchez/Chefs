package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Food;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Food entity.
 */
public interface FoodRepository extends JpaRepository<Food,Long> {

    @Query("select f from Food f where f.normalizaedName like ?1")
    Set<Food> search(String normalizaedName);

    @Query("select f from Food f where f.normalizaedName = ?1 and f.name = ?2")
    Food findOneByNormalizaedNameAndName(String normalizaedName, String name);
}
