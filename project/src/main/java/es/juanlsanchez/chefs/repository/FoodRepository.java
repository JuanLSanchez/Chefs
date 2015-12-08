package es.juanlsanchez.chefs.repository;

import es.juanlsanchez.chefs.domain.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the Food entity.
 */
public interface FoodRepository extends JpaRepository<Food,Long> {

    @Query("select f from Food f where f.normalizaedName like ?1")
    Page<Food> search(String normalizaedName, Pageable pageable);

    @Query("select f from Food f where f.normalizaedName = ?1 and f.name = ?2")
    Food findOneByNormalizaedNameAndName(String normalizaedName, String name);
}
